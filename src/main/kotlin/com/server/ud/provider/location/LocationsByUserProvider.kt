package com.server.ud.provider.location

import com.server.common.utils.DateUtils
import com.server.ud.dao.location.LocationsByUserRepository
import com.server.ud.entities.location.Location
import com.server.ud.entities.location.LocationsByUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LocationsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var locationsByUserRepository: LocationsByUserRepository

    fun save(location: Location) : LocationsByUser? {
        try {
            val locationByUser = LocationsByUser(
                userId = location.userId,
                locationId = location.locationId,
                locationFor = location.locationFor,
                createdAt = location.createdAt,
                zipcode = location.zipcode,
                googlePlaceId = location.googlePlaceId,
                lat = location.lat,
                lng = location.lng,
                name = location.name,
            )
            val savedLocationByUser = locationsByUserRepository.save(locationByUser)
            logger.info("Saved LocationsByUser into cassandra for locationId: ${savedLocationByUser.locationId}")
            return savedLocationByUser
        } catch (e: Exception) {
            logger.error("Saving LocationsByUser into cassandra failed for locationId: ${location.locationId}")
            e.printStackTrace()
            return null
        }
    }

    fun deletePost(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
