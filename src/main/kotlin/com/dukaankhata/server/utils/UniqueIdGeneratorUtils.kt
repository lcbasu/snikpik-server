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
    fun getUniqueId(prefix: String?): String? {
        // Create a new UUID
        var currentId = getCurrentId(prefix)

        // Check in DB and regenerate of required
        try {
            val maximumTryout = 10
            var existingResult = uniqueIdRepository.findById(currentId)
            var currentTryoutCount = 1
            while (existingResult.isPresent && currentTryoutCount < maximumTryout) {
                currentId = getCurrentId(prefix)
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
        return null
    }

    private fun getCurrentId(prefix: String?): String {
        val currentUuid = generateUUIDString(UUID.randomUUID())
        return (prefix?.let { it + currentUuid } ?: currentUuid).toUpperCase()
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
