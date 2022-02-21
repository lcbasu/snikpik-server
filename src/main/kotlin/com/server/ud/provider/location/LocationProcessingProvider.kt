package com.server.ud.provider.location

import com.server.ud.entities.location.Location
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.user.UserV2ProcessingProvider
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
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var userV2ProcessingProvider: UserV2ProcessingProvider

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
            val nearbyZipcodes = locationProvider.getNearbyZipcodes(location.lat, location.lng, location.zipcode)
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
