package com.server.ud.provider.location

import com.server.common.utils.DateUtils
import com.server.ud.dao.es.location.ESLocationRepository
import com.server.ud.entities.es.location.ESLocation
import com.server.ud.entities.location.Location
import com.server.ud.entities.location.getGeoPointData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ESLocationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var esLocationRepository: ESLocationRepository

    fun getLocation(locationId: String): ESLocation? =
        try {
            esLocationRepository.findById(locationId).get()
        } catch (e: Exception) {
            logger.error("Getting Location for $locationId failed.")
            null
        }

    fun save(location: Location) : ESLocation? {
        try {
            val esLocation = ESLocation(
                locationId = location.locationId,
                userId = location.userId,
                createdAt = DateUtils.getEpoch(location.createdAt),
                locationFor = location.locationFor,
                name = location.name,
                lat = location.lat,
                lng = location.lng,
                zipcode = location.zipcode,
                googlePlaceId = location.googlePlaceId,
                geoPoint = location.getGeoPointData(),
                locality = location.locality,
                subLocality = location.subLocality,
                route = location.route,
                city = location.city,
                state = location.state,
                country = location.country,
                countryCode = location.countryCode,
                completeAddress = location.completeAddress,
            )
            val savedESLocation = esLocationRepository.save(esLocation)
            logger.info("Saved location to elastic search locationId: ${savedESLocation.locationId}")
            return savedESLocation
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Saving location to elastic search failed for locationId: ${location.locationId}")
            return null
        }
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
