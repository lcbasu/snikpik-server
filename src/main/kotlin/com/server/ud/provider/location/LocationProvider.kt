package com.server.ud.provider.location

import com.server.common.entities.User
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.ud.dao.location.LocationRepository
import com.server.ud.dto.SaveLocationRequest
import com.server.ud.entities.location.Location
import com.server.ud.service.location.ProcessLocationSchedulerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class LocationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var processLocationSchedulerService: ProcessLocationSchedulerService

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

    fun save(user: User, request: SaveLocationRequest) : Location? {
        try {
            val location = Location(
                locationId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.LOC.name),
                locationFor = request.locationFor,
                userId = user.id,
                createdAt = Instant.now(),
                zipcode = request.zipcode,
                googlePlaceId = request.googlePlaceId,
                lat = request.lat,
                lng = request.lng,
                name = request.name,
            )
            val savedLocation = locationRepository.save(location)
            logger.info("Saved location into cassandra with locationId: ${savedLocation.locationId}")
            processLocationSchedulerService.createLocationProcessingJob(location)
            return savedLocation
        } catch (e: Exception) {
            logger.error("Saved location into cassandra failed for request: $request for userId: ${user.id}")
            e.printStackTrace()
            return null
        }
    }

}
