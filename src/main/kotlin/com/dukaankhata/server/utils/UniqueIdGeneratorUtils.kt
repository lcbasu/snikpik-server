package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.UniqueIdRepository
import com.dukaankhata.server.entities.UniqueId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.abs


@Component
class UniqueIdGeneratorUtils {

    val maximumTryout = 10
    val uuidMinLength = 30
    val uuidMaxLength = 30

    @Autowired
    private lateinit var uniqueIdRepository: UniqueIdRepository

    @Transactional
    fun getUniqueId(prefix: String? = null, onlyNumbers: Boolean? = false, minLength: Int? = uuidMinLength, maxLength: Int? = uuidMaxLength): String {

        // Create a new UUID
        var currentId = getRandomId(prefix, onlyNumbers, minLength, maxLength)

        // Check in DB and regenerate of required
        try {
            var existingResult = uniqueIdRepository.findById(currentId)
            var currentTryoutCount = 1
            while (existingResult.isPresent && currentTryoutCount < maximumTryout) {
                currentId = getRandomId(prefix, onlyNumbers, minLength, maxLength)
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

    private fun getRandomId(prefix: String? = null, onlyNumbers: Boolean?, minLength: Int? = null, maxLength: Int? = null): String {
        if (minLength != null && maxLength != null && minLength > maxLength) {
            error("Min length is greater than max length for UUID generation.")
        }

        val randomUuidOriginal = generateUUIDString(UUID.randomUUID(), onlyNumbers)

        var randomUuid = randomUuidOriginal

        if (minLength != null && randomUuidOriginal.length < minLength) {
            while (randomUuid.length < minLength) {
                randomUuid += generateUUIDString(UUID.randomUUID(), onlyNumbers)
            }
        }

        val uuid = (prefix?.let { it + randomUuid } ?: randomUuid).toUpperCase()

        if (maxLength != null && uuid.length > maxLength) {
            return uuid.substring(0, maxLength - 1)
        }
        return uuid

    }

    // https://stackoverflow.com/a/50275487
    fun generateUUIDString(uuid: UUID, onlyNumbers: Boolean?): String {
        val isOnlyNumbersAskedFor = onlyNumbers?.let { it } ?: false
        return if (isOnlyNumbersAskedFor) {
            //java.lang.String.format("%040d", BigInteger(uuid.toString().replace("-", ""), 16))
            abs(((uuid.mostSignificantBits shr 32) +
                (uuid.mostSignificantBits shr 16) +
                uuid.mostSignificantBits +
                (uuid.leastSignificantBits shr 48) +
                uuid.leastSignificantBits)).toString()
        } else {
            digits(uuid.mostSignificantBits shr 32, 8) +
                digits(uuid.mostSignificantBits shr 16, 4) +
                digits(uuid.mostSignificantBits, 4) +
                digits(uuid.leastSignificantBits shr 48, 4) +
                digits(uuid.leastSignificantBits, 12)
        }
    }

    /** Returns val represented by the specified number of hex digits.  */
    private fun digits(value: Long, digits: Int): String {
        val hi = 1L shl digits * 4
        return java.lang.Long.toHexString(hi or (value and hi - 1)).substring(1)
    }
}
