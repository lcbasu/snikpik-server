package com.server.ud.provider.location

import com.server.ud.entities.location.Location
import kotlinx.coroutines.runBlocking
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.script.ScriptType
import org.elasticsearch.script.mustache.SearchTemplateRequest
import org.elasticsearch.script.mustache.SearchTemplateResponse
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlinx.coroutines.async

@Component
class LocationProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var esLocationProvider: ESLocationProvider

    @Autowired
    private lateinit var locationsByUserProvider: LocationsByUserProvider

    @Autowired
    private lateinit var locationsByZipcodeProvider: LocationsByZipcodeProvider

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var restHighLevelClient: RestHighLevelClient

    fun processLocation(locationId: String) {
        // 1. Save location into ES
        // 2. Search Locations ES index and find all the possible nearby locations ZIPCODE
        // 3. Save all the zipcodes into nearby_zipcodes_by_zipcode cassandra table
        runBlocking {
            logger.info("Do location processing for locationId: $locationId")
            val esLocation = esLocationProvider.getLocation(locationId)
            if (esLocation != null) {
                // Location already processed.
                logger.info("Location already processed for locationId: $locationId")
                return@runBlocking
            }
            val location = locationProvider.getLocation(locationId) ?: error("No location found for $locationId while processing.")
            val nearbyLocationsFuture = async { saveNearbyLocations(location) }
            val locationsByUserFuture = async { locationsByUserProvider.save(location) }
            val locationsByZipcodeFuture = async { locationsByZipcodeProvider.save(location) }
            locationsByUserFuture.await()
            locationsByZipcodeFuture.await()
            nearbyLocationsFuture.await()
            logger.info("Location processing completed for locationId: $locationId")
        }
    }

    fun saveNearbyLocations(location: Location) {
        runBlocking {
            // 1. Save location into ES
            esLocationProvider.save(location)
            // 2. Get Nearby Zipcodes
            val nearbyZipcodes = getNearbyZipcodes(location)
            logger.info("All nearby zipcodes: ${nearbyZipcodes.joinToString(",")}")
            location.zipcode?.let { nearbyZipcodesByZipcodeProvider.save(it, nearbyZipcodes) }
        }
    }

    fun getNearbyZipcodes(location: Location): Set<String> {
        return try {
            val request = SearchTemplateRequest(SearchRequest("locations"))
            request.scriptType = ScriptType.INLINE
            request.script =
                "{\"aggs\":{\"locations_filter\":{\"filter\":{\"geo_distance\":{\"distance\":\"{{distance_in_km}}\",\"geoPoint\":{\"lat\":{{latitude}},\"lon\":{{longitude}}}}},\"aggs\":{\"zipcodes\":{\"terms\":{\"field\":\"zipcode\"}}}}},\"size\":0}"
            val scriptParams: MutableMap<String, Any> = HashMap()

            // TODO: Make this dynamic by taking this input from user
            scriptParams["distance_in_km"] = "300km"
            scriptParams["latitude"] = location.lat.toString()
            scriptParams["longitude"] = location.lng.toString()
            request.scriptParams = scriptParams
            val response: SearchTemplateResponse = restHighLevelClient.searchTemplate(request, RequestOptions.DEFAULT)
            ((response.response.aggregations.asList()[0] as ParsedFilter).aggregations.asList()[0] as Terms).buckets.map {
                it.keyAsString
            }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

}
