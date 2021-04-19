package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.UniqueIdRepository
import com.dukaankhata.server.entities.UniqueId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Component
class UniqueIdGeneratorUtils {

    @Autowired
    private lateinit var uniqueIdRepository: UniqueIdRepository

    @Transactional
    fun getUniqueId(prefix: String? = null, maxLength: Int? = 10): String {
        // Create a new UUID
        var currentId = getRandomId(prefix, maxLength)

        // Check in DB and regenerate of required
        try {
            val maximumTryout = 10
            var existingResult = uniqueIdRepository.findById(currentId)
            var currentTryoutCount = 1
            while (existingResult.isPresent && currentTryoutCount < maximumTryout) {
                currentId = getRandomId(prefix, maxLength)
                existingResult = uniqueIdRepository.findById(currentId)
                currentTryoutCount += 1
            }
            // Save the unique ID if not already present
            if (existingResult.isPresent.not()) {
                uniqueIdRepository.saveAndFlush(UniqueId(currentId))
                // Return the unique ID
                return currentId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        error("Failed to generate Unique ID")
    }

    private fun getRandomId(prefix: String? = null, maxLength: Int? = null): String {
        val randomUuidOriginal = generateUUIDString(UUID.randomUUID())
        val randomUuid = if (maxLength != null && randomUuidOriginal.length > maxLength) {
            randomUuidOriginal.substring(0, maxLength - 1)
        } else {
            randomUuidOriginal
        }
        return (prefix?.let { it + randomUuid } ?: randomUuid).toUpperCase()
    }

    // https://stackoverflow.com/a/50275487
    fun generateUUIDString(uuid: UUID): String {
        return digits(uuid.mostSignificantBits shr 32, 8) +
            digits(uuid.mostSignificantBits shr 16, 4) +
            digits(uuid.mostSignificantBits, 4) +
            digits(uuid.leastSignificantBits shr 48, 4) +
            digits(uuid.leastSignificantBits, 12)
    }

    /** Returns val represented by the specified number of hex digits.  */
    private fun digits(value: Long, digits: Int): String {
        val hi = 1L shl digits * 4
        return java.lang.Long.toHexString(hi or (value and hi - 1)).substring(1)
    }
}
