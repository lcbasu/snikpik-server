package com.server.dk.dto

import com.server.dk.entities.Attendance
import com.server.dk.entities.AttendanceByAdmin
import com.server.dk.enums.AttendanceType
import com.server.dk.enums.PunchType
import com.server.common.enums.SelfieType
import com.server.dk.enums.ValueUnitType
import com.server.common.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveAttendanceRequest(
    val employeeId: String,
    val companyId: String,
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
    val companyId: String,
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
    val companyId: String,
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
    val employeeId: String,
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
    val companyId: String,
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceSummaryForEmployeeRequest(
    val employeeId: String,
    val forYear: Int,
    val forMonth: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceInfoDataResponse (
    val attendanceType: AttendanceType,
    val displayText: String,
    val dateNumber: Int,
    val dateText: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttendanceSummaryForEmployeeResponse (
    val employee: SavedEmployeeResponse,
    // Key: forDate, Value: Type
    val attendancesReport: Map<String, AttendanceInfoDataResponse>,
    val attendanceTypeAggregate: List<AttendanceTypeAggregateResponse>,
)


fun Attendance.toSavedAttendanceResponse(): SavedAttendanceResponse {
    this.apply {
        return SavedAttendanceResponse(
            serverId = id,
            employee = employee!!.toSavedEmployeeResponse(),
            company = company!!.toSavedCompanyResponse(),
            forDate = forDate,
            punchAt = DateUtils.getEpoch(punchAt),
            punchType = punchType,
            punchBy = punchBy?.id ?: "",
            selfieUrl = selfieUrl ?: "",
            selfieType = selfieType ?: SelfieType.NONE,
            locationLat = locationLat ?: 0.0,
            locationLong = locationLong ?: 0.0,
            locationName = locationName ?: "")
    }
}

fun AttendanceByAdmin.toSavedAttendanceByAdminResponse(): SavedAttendanceByAdminResponse {
    return SavedAttendanceByAdminResponse(
        serverId = id?.companyId.toString() + "__" + id?.employeeId.toString() + "__" + id?.forDate,
        employee = employee!!.toSavedEmployeeResponse(),
        company = company!!.toSavedCompanyResponse(),
        forDate = id?.forDate ?: "",
        attendanceType = attendanceType,
        addedBy = addedBy?.id ?: "",
        workingMinutes = workingMinutes)
}
