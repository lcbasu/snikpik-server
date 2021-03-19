package com.dukaankhata.server.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private const val standardFormatter = "yyyy-MM-dd"
    fun parseStandardDate(forDate: String): LocalDateTime {
        return LocalDate.parse(forDate, DateTimeFormatter.ofPattern(standardFormatter)).atStartOfDay()
    }
}
