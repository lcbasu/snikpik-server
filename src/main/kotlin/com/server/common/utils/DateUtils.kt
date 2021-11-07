package com.server.common.utils

import ReportDuration
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.math.floor

object DateUtils {
    private const val standardForDateFormatter = "yyyy-MM-dd"
    private const val standardForMonthFormatter = "yyyy-MM"
    private const val standardTimeZoneId = "UTC"
    private val standardZoneOffset = ZoneOffset.UTC

    fun parseStandardDate(forDate: String): LocalDateTime =
        LocalDate.parse(forDate, DateTimeFormatter.ofPattern(standardForDateFormatter)).atStartOfDay()

    fun parseEpochInMilliseconds(epochInMilliSeconds: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(epochInMilliSeconds / 1000), ZoneId.of(standardTimeZoneId))

    fun getEpoch(dateTime: LocalDateTime?): Long =
        dateTime?.toEpochSecond(standardZoneOffset) ?: 0

    fun getEpoch(forInstant: Instant): Long =
        getEpoch(getInstantDateTime(forInstant))

    fun getCurrentTimeInEpoch(): Long = dateTimeNow().toEpochSecond(standardZoneOffset)

    fun dateTimeNow(): LocalDateTime =
        LocalDateTime.now(ZoneId.of(standardTimeZoneId))

    fun getInstantFromLocalDateTime(localDateTime: LocalDateTime): Instant =
        localDateTime.toInstant(standardZoneOffset)

    fun getInstantNow(): Instant =
        dateTimeNow().toInstant(standardZoneOffset)

    /**
     *
     * Instant for the start of the day
     *
     * */
    fun getInstantToday(): Instant =
        dateTimeNow().toLocalDate().atStartOfDay().toInstant(standardZoneOffset)

    fun getInstantDate(forInstant: Instant): Instant =
         parseEpochInMilliseconds(forInstant.toEpochMilli()).toLocalDate().atStartOfDay().toInstant(standardZoneOffset)

    fun getInstantDateTime(forInstant: Instant): LocalDateTime =
        parseEpochInMilliseconds(forInstant.toEpochMilli())

    fun toStringForDate(dateTime: LocalDateTime): String =
        dateTime.format(DateTimeFormatter.ofPattern(standardForDateFormatter))

    fun toStringForDate(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern(standardForDateFormatter))

    fun toStringForDateDefault(): String =
        dateTimeNow().format(DateTimeFormatter.ofPattern(standardForDateFormatter))

    fun toStringForMonth(dateTime: LocalDateTime): String =
        dateTime.format(DateTimeFormatter.ofPattern(standardForMonthFormatter))

    fun toStringForMonth(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern(standardForMonthFormatter))

    fun toLocalDate(dateTime: LocalDateTime): LocalDate = dateTime.toLocalDate()

    fun getDatesBetweenInclusiveOfStartAndEndDates(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<LocalDate> {
        // Adding one day to the end date to include the last date in the list as well

//        val startDate = toStringDate(startDateTime)
//        val endDate = toStringDate(endDateTime)

//        var daysBetween: Long = ChronoUnit.DAYS.between(startDateTime, endDateTime) + 1

        val allDates = mutableListOf<LocalDate>()
        var incrementingDate = toLocalDate(startDateTime)
        val endDate = toLocalDate(endDateTime)
        check(!incrementingDate.isAfter(endDate)) { "start date must be before or equal to end date" }
        while (!incrementingDate.isAfter(endDate)) {
            allDates.add(incrementingDate)
            incrementingDate = incrementingDate.plusDays(1)
        }
        // datesUntil failed to build
        return allDates//toLocalDate(startDateTime).datesUntil(toLocalDate(endDateTime.plusDays(1))).toList()
    }

    fun getHourOrMinAsString(hourOrMin: Int): String {
        return if (hourOrMin > 9) "$hourOrMin" else "0$hourOrMin"
    }

    fun getMinutesToHourString(minutes: Int): String {
        val hours = floor(minutes.toDouble() / 60.0).toInt()
        return getHourOrMinAsString(hours) + ":" + getHourOrMinAsString(minutes - (hours*60))
    }

    fun getDateNumber(strDate: String): Int {
        return parseStandardDate(strDate).dayOfMonth
    }

    fun getWeekName(strDate: String): String {
        return parseStandardDate(strDate).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
    }

    fun getRandomDateInMonth(forYear: Int, forMonth: Int): LocalDateTime {
        return LocalDateTime.of(forYear, forMonth, 20, 0, 0, 0, 0)
    }

    fun getReportDuration(forYear: Int, forMonth: Int): ReportDuration {
        // Choosing a random date in middle to select correct start and end month
        val randomDateInMonth = getRandomDateInMonth(forYear = forYear, forMonth = forMonth)
        // First day of month
        val startTime = getStartDateForMonthWithDate(randomDateInMonth)

        // Last day of month or today in case of current month
        var endTime = getLastDateForMonthWithDate(randomDateInMonth)
        if (endTime.isAfter(DateUtils.dateTimeNow())) {
            endTime = DateUtils.dateTimeNow()
        }
        return ReportDuration(
            startTime = startTime,
            endTime = endTime
        )
    }

    fun getStartDateForMonthWithDate(withDate: LocalDateTime) : LocalDateTime {
        return withDate.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay()
    }

    fun getLastDateForMonthWithDate(withDate: LocalDateTime) : LocalDateTime {
        return withDate.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX)
    }

    fun toDate(fromLocalDateTime: LocalDateTime): Date {
        return Date.from(fromLocalDateTime.atZone(ZoneId.of(standardTimeZoneId)).toInstant())
    }

    fun getDateInPast(daysInPast: Long) : LocalDateTime {
        return dateTimeNow().minusDays(daysInPast)
    }
}
