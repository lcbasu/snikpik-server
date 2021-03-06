package com.server.ud.provider.location

import com.server.ud.dao.location.NearbyZipcodesByZipcodeRepository
import com.server.ud.entities.location.NearbyZipcodesByZipcode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NearbyZipcodesByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeRepository: NearbyZipcodesByZipcodeRepository

    fun getNearbyZipcodesByZipcode(zipcode: String): List<NearbyZipcodesByZipcode> =
        try {
            nearbyZipcodesByZipcodeRepository.findAllByZipcode(zipcode)
        } catch (e: Exception) {
            logger.error("Getting NearbyZipcodesByZipcode for $zipcode failed.")
            e.printStackTrace()
            emptyList()
        }

    fun save(forZipcode: String, nearbyZipcodes: Set<String>) : List<NearbyZipcodesByZipcode> {
        try {
            if (nearbyZipcodes.isEmpty()) return emptyList()
            val savedLocationByZipcode = nearbyZipcodesByZipcodeRepository.saveAll(
                nearbyZipcodes.map {
                    NearbyZipcodesByZipcode(
                        zipcode = forZipcode,
                        nearbyZipcode = it
                    )
                }
            )
            logger.info("Saved NearbyZipcodesByZipcode into cassandra for Zipcode: $forZipcode with nearbyZipcodes: ${nearbyZipcodes.joinToString(",")}")
            return savedLocationByZipcode
        } catch (e: Exception) {
            logger.error("Saving NearbyZipcodesByZipcode into cassandra failed for Zipcode: $forZipcode with nearbyZipcodes: ${nearbyZipcodes.joinToString(",")}")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
