package com.server.ud.provider.location

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.RandomIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.location.LocationRepository
import com.server.ud.dto.CitiesLocationResponse
import com.server.ud.dto.SaveLocationRequest
import com.server.ud.dto.toCityLocationDataResponse
import com.server.ud.entities.location.Location
import com.server.ud.enums.LocationFor
import com.server.ud.provider.cache.UDCacheProvider
import com.server.ud.provider.deferred.DeferredProcessingProvider
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class LocationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val randomLocationId = "LOC_RANDOM"
    private val randomLocationZipcode = "ZZZZZZ"
    private val randomLocationName = "Global"

    @Autowired
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var randomIdProvider: RandomIdProvider

    @Autowired
    private lateinit var deferredProcessingProvider: DeferredProcessingProvider

    @Autowired
    private lateinit var udCacheProvider: UDCacheProvider

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
                locationId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.LOC),
                locationFor = request.locationFor,
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                zipcode = request.zipcode,
                googlePlaceId = request.googlePlaceId,
                lat = request.lat,
                lng = request.lng,
                name = request.name,
            )
            val savedLocation = locationRepository.save(location)
            logger.info("Saved location into cassandra with locationId: ${savedLocation.locationId}")
            deferredProcessingProvider.deferProcessingForLocation(savedLocation.locationId)
            return savedLocation
        } catch (e: Exception) {
            logger.error("Saved location into cassandra failed for request: $request for userId: ${userId}")
            e.printStackTrace()
            return null
        }
    }

    // To be used for cases where there are no location of post or for the user
    fun getOrSaveRandomLocation(userId: String, locationFor: LocationFor) : Location? {
        try {
            // Get
            getLocation(randomLocationId)?.let {
                return it
            }

            // Save will be called only once
            // Keeping it here so that if we reset the DB,
            // this save happens and the future calls
            // do not happen
            val location = Location(
                locationId = randomLocationId,
                locationFor = locationFor,
                userId = userId,
                createdAt = Instant.now(),
                zipcode = randomLocationZipcode,
                name = randomLocationName,
            )
            val savedLocation = locationRepository.save(location)
            logger.info("Saved location into cassandra with locationId: ${savedLocation.locationId}")
            deferredProcessingProvider.deferProcessingForLocation(savedLocation.locationId)
            return savedLocation
        } catch (e: Exception) {
            logger.error("Saved location into cassandra failed locationFor: $locationFor for userId: $userId")
            e.printStackTrace()
            return null
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

}
