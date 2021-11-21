package com.server.common.provider

import com.datastax.oss.driver.api.core.uuid.Uuids.timeBased
import com.server.common.enums.ReadableIdPrefix
import org.springframework.stereotype.Component
import java.util.*
import kotlin.math.abs

@Component
class RandomIdProvider {

    fun getRandomId(prefix: String? = null, onlyNumbers: Boolean? = false, minLength: Int? = null, maxLength: Int? = null): String {
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

    fun getTimeBasedRandomId() : String {
        return timeBased().toString()
    }

    fun getRandomIdFor(prefix: ReadableIdPrefix) : String {
        return "${prefix.name}${UUID.randomUUID().toString()}"
    }
}
