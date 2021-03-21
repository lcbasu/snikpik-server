package com.dukaankhata.server.utils

import java.time.*
import java.time.format.DateTimeFormatter

object DateUtils {
    private const val standardFormatter = "yyyy-MM-dd"
    private const val standardTimeZoneId = "UTC"
    fun parseStandardDate(forDate: String): LocalDateTime {
        return LocalDate.parse(forDate, DateTimeFormatter.ofPattern(standardFormatter)).atStartOfDay()
    }

    fun parseEpochInMilliseconds(epochInMilliSeconds: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochInMilliSeconds/1000), ZoneId.of(standardTimeZoneId))
    }

    fun getEpoch(date: LocalDateTime?): Long {
        return date?.toEpochSecond(ZoneOffset.UTC) ?: 0
    }

    fun dateTimeNow(): LocalDateTime = LocalDateTime.now(ZoneId.of(standardTimeZoneId))
}
