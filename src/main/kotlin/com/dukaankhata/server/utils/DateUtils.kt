package com.dukaankhata.server.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {
    private const val standardFormatter = "yyyy-MM-dd"
    fun parseStandardDate(forDate: String): LocalDateTime {
        return LocalDate.parse(forDate, DateTimeFormatter.ofPattern(standardFormatter)).atStartOfDay()
    }

    fun parseEpochInMilliseconds(epoch: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch/1000), ZoneId.of("UTC"))
    }
}
