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
    val metaData: List<AttendanceUnit> = emptyList(),
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
data class AttendanceUnit(
    val attendanceType: AttendanceType,
    val valueUnitType: ValueUnitType, // DAY or minutes worked or hours etc
    val value: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeeAttendanceSummaryResponse(
    val employee: SavedEmployeeResponse,
    // Could be different than start and end time of company if the employee was hired
    // on a date which is different that some other employee
    val startTime: Long,
    val endTime: Long,
    val aggregate: List<AttendanceUnit>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceSummaryResponse (
    val company: SavedCompanyResponse,
    val startTime: Long,
    val endTime: Long,
    val employeesReport: List<AttendanceReportForEmployeeResponse>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceReportForEmployeeResponse (
    val employee: SavedEmployeeResponse,
    val startTime: Long,
    val endTime: Long,
    val totalDay: Int,
    val presentDays: Int,
    val absentDays: Int,
    val halfDays: Int,
    val paidHolidays: Int,
    val nonPaidHolidays: Int,
    val overtimeMinutes: Int,
    val overtimeAmountInPaisa: Long,
    val lateFineMinutes: Int,
    val lateFineAmountInPaisa: Long,
    val companyWorkingMinutesPerDay: Int,
)
