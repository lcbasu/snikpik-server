package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.AttendanceType
import com.dukaankhata.server.enums.PunchType
import com.dukaankhata.server.enums.SelfieType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveAttendanceRequest(
    val employeeId: Long,
    val companyId: Long,
    val forDate: String,
    val punchAt: Long,
    val punchType: PunchType,
    val punchBy: String,
    val selfieUrl: String?,
    val selfieType: SelfieType?,
    val locationLat: Double?,
    val locationLong: Double?,
    val locationName: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedAttendanceResponse(
    val serverId: String,
    val employee: SavedEmployeeResponse,
    val company: SavedCompanyResponse,
    val forDate: String,
    val punchAt: Long,
    val punchType: PunchType,
    val punchBy: String,
    val selfieUrl: String,
    val selfieType: SelfieType,
    val locationLat: Double,
    val locationLong: Double,
    val locationName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GetAttendancesRequest(
    val companyId: Long,
    val forDates: Set<String>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeeAttendancesResponse(
    val employee: SavedEmployeeResponse,
    val attendances: List<SavedAttendanceResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendancesDateResponse(
    val forDate: String, // YYYY-MM-DD
    val employeesAttendances: List<EmployeeAttendancesResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendancesResponse(
    val company: SavedCompanyResponse,
    val attendancesDateResponses: List<AttendancesDateResponse>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeeAttendanceResponse(
    val employee: SavedEmployeeResponse,
    val forDate: String, // YYYY-MM-DD
    val workingHoursInMinutes: Int,
    val attendanceType: AttendanceType,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceTypeAggregateResponse(
    val count: Int,
    val attendanceType: AttendanceType,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceInfoRequest(
    val companyId: Long,
    val forDate: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceInfoResponse(
    val company: SavedCompanyResponse,
    val forDate: String, // YYYY-MM-DD
    val employeesAttendance: List<EmployeeAttendanceResponse>,
    val attendanceTypeAggregate: List<AttendanceTypeAggregateResponse>,
)
