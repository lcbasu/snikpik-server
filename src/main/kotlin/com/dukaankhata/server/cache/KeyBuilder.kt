package com.dukaankhata.server.cache

import com.dukaankhata.server.dto.AttendanceSummaryForEmployeeRequest
import com.dukaankhata.server.utils.CommonUtils.STRING_SEPARATOR

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
}
