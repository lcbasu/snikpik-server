package com.server.ud.provider.location

import com.server.common.utils.DateUtils
import com.server.ud.dao.location.LocationsByZipcodeRepository
import com.server.ud.entities.location.Location
import com.server.ud.entities.location.LocationsByZipcode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LocationsByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var locationsByZipcodeRepository: LocationsByZipcodeRepository

    fun save(location: Location) : LocationsByZipcode? {
        try {
            if (location.zipcode == null) {
                logger.info("Saving LocationsByZipcode into cassandra for locationId: ${location.locationId} failed as the zipcode is missing")
                return null
            }
            val locationByZipcode = LocationsByZipcode(
                zipcode = location.zipcode!!,
                forDate = DateUtils.toStringForDateDefault(),
                userId = location.userId,
                locationId = location.locationId,
                locationFor = location.locationFor,
                createdAt = location.createdAt,
                googlePlaceId = location.googlePlaceId,
                lat = location.lat,
                lng = location.lng,
                name = location.name,
            )
            val savedLocationByZipcode = locationsByZipcodeRepository.save(locationByZipcode)
            logger.info("Saved LocationsByZipcode into cassandra for locationId: ${savedLocationByZipcode.locationId}")
            return savedLocationByZipcode
        } catch (e: Exception) {
            logger.error("Saving LocationsByZipcode into cassandra failed for locationId: ${location.locationId}")
            e.printStackTrace()
            return null
        }
    }
}
