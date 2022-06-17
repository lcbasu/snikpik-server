package com.server.ud.provider.location

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.location.LocationRepository
import com.server.ud.dto.CitiesLocationResponse
import com.server.ud.dto.SaveLocationRequest
import com.server.ud.dto.toCityLocationDataResponse
import com.server.ud.dto.toSaveLocationRequest
import com.server.ud.entities.location.Location
import com.server.ud.enums.LocationFor
import com.server.ud.provider.cache.UDCacheProvider
import com.server.ud.provider.es.ESProvider
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.user.UserV2ProcessingProvider
import com.server.common.utils.CommonUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.script.ScriptType
import org.elasticsearch.script.mustache.SearchTemplateRequest
import org.elasticsearch.script.mustache.SearchTemplateResponse
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.random.Random

@Component
class LocationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var udCacheProvider: UDCacheProvider

    @Autowired
    private lateinit var esProvider: ESProvider

    @Autowired
    private lateinit var esLocationProvider: ESLocationProvider

    @Autowired
    private lateinit var locationsByUserProvider: LocationsByUserProvider

    @Autowired
    private lateinit var locationsByZipcodeProvider: LocationsByZipcodeProvider

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var userV2ProcessingProvider: UserV2ProcessingProvider

    fun getLocation(locationId: String): Location? =
        try {
            val locations = locationRepository.findAllByLocationId(locationId)
            if (locations.size > 1) {
                error("More than one location has same locationId: $locationId")
            }
            locations.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting Location for $locationId failed.")
            e.printStackTrace()
            null
        }

    fun save(userId: String, request: SaveLocationRequest) : Location? {
        try {
            val location = Location(
                locationId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.LOC.name),
                locationFor = request.locationFor,
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                zipcode = request.zipcode,
                googlePlaceId = request.googlePlaceId,
                lat = request.lat,
                lng = request.lng,
                name = request.name,
                locality = request.locality,
                subLocality = request.subLocality,
                route = request.route,
                city = request.city,
                state = request.state,
                country = request.country,
                countryCode = request.countryCode,
                completeAddress = request.completeAddress,
            )
            val savedLocation = locationRepository.save(location)
            logger.info("Saved location into cassandra with locationId: ${savedLocation.locationId}")
            udJobProvider.scheduleProcessingForLocation(savedLocation.locationId)
            return savedLocation
        } catch (e: Exception) {
            logger.error("Saved location into cassandra failed for request: $request for userId: ${userId}")
            e.printStackTrace()
            return null
        }
    }

    // To be used for cases where there are no location of post or for the user
    fun getOrSaveRandomLocation(userId: String, locationFor: LocationFor) : Location? {
        return try {
            // Get
            val existing = getLocation(CommonUtils.randomLocationId)

            if (existing != null) {
                logger.info("Get random location from cassandra")
                existing
            } else {
                // Save will be called only once
                // Keeping it here so that if we reset the DB,
                // this save happens and the future calls
                // do not happen
                val location = Location(
                    locationId = CommonUtils.randomLocationId,
                    locationFor = locationFor,
                    userId = userId,
                    createdAt = Instant.now(),
                    zipcode = CommonUtils.randomLocationZipcode,
                    name = CommonUtils.randomLocationName,
                )
                val savedLocation = locationRepository.save(location)
                logger.info("Saved random location into cassandra with locationId: ${savedLocation.locationId}")
                udJobProvider.scheduleProcessingForLocation(savedLocation.locationId)
                savedLocation
            }
        } catch (e: Exception) {
            logger.error("Saving location into cassandra failed locationFor: $locationFor for userId: $userId")
            e.printStackTrace()
            null
        }
    }

    fun getCitiesLocationData(): CitiesLocationResponse {
        return runBlocking {
            CitiesLocationResponse(
                cities = udCacheProvider.getCitiesData().await()?.values?.map {
                    it.toCityLocationDataResponse()
                } ?: emptyList()
            )
        }
    }

    fun saveCitiesDataIntoDb(userId: String) {
        runBlocking {
            udCacheProvider.getCitiesData().await()?.values?.map {
                save(userId, it.toSaveLocationRequest(LocationFor.USER))
            }
        }
    }

    fun getSampleLocationRequestsFromCities(locationFor: LocationFor): List<SaveLocationRequest> {
        return runBlocking {
            val cities = udCacheProvider.getCitiesData().await()?.values?.filterNotNull() ?: emptyList()
            cities.shuffled().map {
                it.toSaveLocationRequest(locationFor)
            }
        }
    }

    fun getNearbyZipcodes(lat: Double?, lng: Double?, originalZipcode: String? = null): Set<String> {
        return (try {
            val request = SearchTemplateRequest(SearchRequest("locations"))
            request.scriptType = ScriptType.INLINE
            request.script =
                "{\"aggs\":{\"locations_filter\":{\"filter\":{\"geo_distance\":{\"distance\":\"{{distance_in_km}}\",\"geoPoint\":{\"lat\":{{latitude}},\"lon\":{{longitude}}}}},\"aggs\":{\"zipcodes\":{\"terms\":{\"field\":\"zipcode\"}}}}},\"size\":0}"
            val scriptParams: MutableMap<String, Any> = HashMap()

            // TODO: Make this dynamic by taking this input from user
            scriptParams["distance_in_km"] = "150km"
            scriptParams["latitude"] = lat.toString()
            scriptParams["longitude"] = lng.toString()
            request.scriptParams = scriptParams
            val response: SearchTemplateResponse? = esProvider.executeRequest(request)
            val nearbyZipcodes = response?.let {
                // Send the random location zip code for nearby zipcode
                // randomly so that we keep creating a fallback zipcode
                // For all types of feed
                val shouldAddRandomZipcode = Random.nextInt(1, 25) % 5 == 0
                ((response.response.aggregations.asList()[0] as ParsedFilter).aggregations.asList()[0] as Terms).buckets.map {
                    it.keyAsString
                }.toSet() + (if (shouldAddRandomZipcode) {
                    setOf(CommonUtils.randomLocationZipcode)
                } else {
                    emptySet()
                })
            } ?: emptySet()
            val computedZipcode = if (nearbyZipcodes.isEmpty()) {
                // In case of no nearby zipcodes, add the random zipcode
                setOf(CommonUtils.randomLocationZipcode)
            } else {
                nearbyZipcodes
            }
            computedZipcode.plus(originalZipcode)
        } catch (e: Exception) {
            setOf(originalZipcode)
        }).filterNotNull().toSet()
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }

    fun processLocation(locationId: String) {
        // 1. Save location into ES
        // 2. Search Locations ES index and find all the possible nearby locations ZIPCODE
        // 3. Save all the zipcodes into nearby_zipcodes_by_zipcode cassandra table
        GlobalScope.launch {
            logger.info("Start: location processing for locationId: $locationId")
            val esLocation = esLocationProvider.getLocation(locationId)
            if (esLocation != null) {
                // Location already processed.
                logger.info("Location already processed for locationId: $locationId")
                return@launch
            }
            val location = getLocation(locationId) ?: error("No location found for $locationId while processing.")
            // Heavy Job. So let it process on its own
            nearbyLocationLevelProcessing(location)
            val locationsByUserFuture = async { locationsByUserProvider.save(location) }
            val locationsByZipcodeFuture = async { locationsByZipcodeProvider.save(location) }
            locationsByUserFuture.await()
            locationsByZipcodeFuture.await()
            logger.info("End: location processing for locationId: $locationId")
        }
    }

    fun nearbyLocationLevelProcessing(location: Location) {
        GlobalScope.launch {
            logger.info("Start: Save nearby locations processing for locationId: ${location.locationId}")
            // 1. Save location into ES
            esLocationProvider.save(location)
            // 2. Get Nearby Zipcodes
            val nearbyZipcodes = getNearbyZipcodes(location.lat, location.lng, location.zipcode)
            logger.info("All nearby zipcodes: ${nearbyZipcodes.joinToString(",")} for source zipcode: ${location.zipcode}")
            location.zipcode?.let { nearbyZipcodesByZipcodeProvider.save(it, nearbyZipcodes) }


            // Very-Very Important
            /**
             *
             * We can not save a post to all possible locations. That is impossible.
             *
             * So instead, what we do is, we save a post to all the nearby locations of
             * any new location that is saved on our platform.
             *
             *
             * */
            doNearbyFeedRelatedProcessingForNewLocation(location, nearbyZipcodes)

            logger.info("End: Save nearby locations processing for locationId: ${location.locationId}")
        }
    }

    private fun doNearbyFeedRelatedProcessingForNewLocation(
        location: Location,
        nearbyZipcodes: Set<String>
    ) {
        // Get all the posts that are near to this location
        // and save them for this zipcode
        postProvider.processPostForNewNearbyLocation(
            originalLocation = location,
            nearbyZipcodes = nearbyZipcodes
        )

        // Do same thing for MarketplaceUser
        userV2ProcessingProvider.processUserDataForNewNearbyLocation(
            originalLocation = location,
            nearbyZipcodes = nearbyZipcodes
        )
    }

}
