package com.server.dk.cache

import com.server.dk.dto.AttendanceSummaryForEmployeeRequest
import com.server.dk.utils.CommonUtils.STRING_SEPARATOR
import org.apache.commons.lang3.StringUtils

object KeyBuilder {
    fun getKeyForAttendanceSummaryForEmployeeResponseCache(attendanceSummaryForEmployeeRequest: AttendanceSummaryForEmployeeRequest): String =
        "${attendanceSummaryForEmployeeRequest.employeeId}${STRING_SEPARATOR}${attendanceSummaryForEmployeeRequest.forYear}${STRING_SEPARATOR}${attendanceSummaryForEmployeeRequest.forMonth}"

    fun parseKeyForAttendanceSummaryForEmployeeResponseCache(key: String): AttendanceSummaryForEmployeeRequest {
        val (employeeId, forYear, forMonth) = key.split(STRING_SEPARATOR)
        return AttendanceSummaryForEmployeeRequest(
            employeeId = employeeId,
            forYear = forYear.toInt(),
            forMonth = forMonth.toInt()
        )
    }

    fun getKeyForThirdPartyImageSearchResponseCache(query: String): String =
        StringUtils.normalizeSpace(query).replace(" ", STRING_SEPARATOR)

    fun parseKeyForThirdPartyImageSearchResponseCache(key: String) =
        key.replace(STRING_SEPARATOR, " ")
}
