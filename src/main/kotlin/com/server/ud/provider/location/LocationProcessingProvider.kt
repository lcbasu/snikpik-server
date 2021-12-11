package com.server.ud.provider.location

import com.server.ud.entities.location.Location
import com.server.ud.provider.es.ESProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
    private lateinit var esProvider: ESProvider

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
            val location = locationProvider.getLocation(locationId) ?: error("No location found for $locationId while processing.")
            val nearbyLocationsFuture = async { saveNearbyLocations(location) }
            val locationsByUserFuture = async { locationsByUserProvider.save(location) }
            val locationsByZipcodeFuture = async { locationsByZipcodeProvider.save(location) }
            locationsByUserFuture.await()
            locationsByZipcodeFuture.await()
            nearbyLocationsFuture.await()
            logger.info("End: location processing for locationId: $locationId")
        }
    }

    fun saveNearbyLocations(location: Location) {
        GlobalScope.launch {
            logger.info("Start: Save nearby locations processing for locationId: ${location.locationId}")
            // 1. Save location into ES
            esLocationProvider.save(location)
            // 2. Get Nearby Zipcodes
            val nearbyZipcodes = esProvider.getNearbyZipcodes(location)
            logger.info("All nearby zipcodes: ${nearbyZipcodes.joinToString(",")} for source zipcode: ${location.zipcode}")
            location.zipcode?.let { nearbyZipcodesByZipcodeProvider.save(it, nearbyZipcodes) }
            logger.info("End: Save nearby locations processing for locationId: ${location.locationId}")
        }
    }

}
