package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.AttendanceType
import com.dukaankhata.server.enums.PunchType
import com.dukaankhata.server.enums.SelfieType
import com.dukaankhata.server.enums.ValueUnitType
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class MarkAttendanceRequest(
    val attendanceType: AttendanceType,
    val employeeId: Long,
    val forDate: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedAttendanceByAdminResponse(
    val serverId: String,
    val employee: SavedEmployeeResponse,
    val company: SavedCompanyResponse,
    val forDate: String,
    val attendanceType: AttendanceType,
    val workingMinutes: Int,
    val addedBy: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceSummaryRequest(
    val companyId: Long,
    val forYear: Int,
    val forMonth: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceAggregateUnit(
    val attendanceType: AttendanceType,
    val valueUnitType: ValueUnitType, // DAY or minutes worked or hours etc
    val value: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceAggregate(
    val aggregate: List<AttendanceAggregateUnit>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeeAttendanceSummaryResponse(
    val employee: SavedEmployeeResponse,
    // Could be different than start and end time of company if the employee was hired
    // on a date which is different that some other employee
    val startTime: Long,
    val endTime: Long,
    val attendanceAggregates: AttendanceAggregate
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceSummaryResponse(
    val company: SavedCompanyResponse,
    val startTime: Long,
    val endTime: Long,
    val monthAggregate: AttendanceAggregate,
    val employeesAttendances: List<EmployeeAttendanceSummaryResponse>,
)
