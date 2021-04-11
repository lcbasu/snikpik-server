package com.dukaankhata.server.service.converter

import AttendanceReportForEmployee
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.AttendanceType
import com.dukaankhata.server.enums.PunchType
import com.dukaankhata.server.enums.SelfieType
import com.dukaankhata.server.enums.ValueUnitType
import com.dukaankhata.server.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AttendanceServiceConverter {

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    fun getSavedAttendanceResponse(attendance: Attendance?): SavedAttendanceResponse {
        return SavedAttendanceResponse(
            serverId = attendance?.id?.toString() ?: "-1",
            employee = employeeServiceConverter.getSavedEmployeeResponse(attendance?.employee),
            company = companyServiceConverter.getSavedCompanyResponse(attendance?.company),
            forDate = attendance?.forDate ?: "",
            punchAt = DateUtils.getEpoch(attendance?.punchAt),
            punchType = attendance?.punchType ?: PunchType.NONE,
            punchBy = attendance?.punchBy?.id ?: "",
            selfieUrl = attendance?.selfieUrl ?: "",
            selfieType = attendance?.selfieType ?: SelfieType.NONE,
            locationLat = attendance?.locationLat ?: 0.0,
            locationLong = attendance?.locationLong ?: 0.0,
            locationName = attendance?.locationName ?: "")
    }

    fun getAttendancesResponse(company: Company, attendances: List<Attendance>): AttendancesResponse {
        val attendancesDateResponses = mutableListOf<AttendancesDateResponse>()
        attendances.groupBy { it.forDate }.map { attendancesForDate ->
            attendancesForDate.key.let {
                val attendancesForGivenDate = attendancesForDate.value.groupBy { it.employee }.map { employeeAttendance ->
                    val employee = employeeAttendance.key
                    val employeeAttendances = employeeAttendance.value
                    EmployeeAttendancesResponse(
                        employee = employeeServiceConverter.getSavedEmployeeResponse(employee),
                        attendances = employeeAttendances.map { getSavedAttendanceResponse(it) }
                    )
                }
                attendancesDateResponses.add(
                    AttendancesDateResponse(
                        forDate = it,
                        employeesAttendances = attendancesForGivenDate
                    )
                )
            }
        }
        return AttendancesResponse(
            company = companyServiceConverter.getSavedCompanyResponse(company),
            attendancesDateResponses = attendancesDateResponses
        )
    }

    fun getEmployeeAttendanceResponse(employee: Employee,
                                      workingMinutes: Int,
                                      attendanceType: AttendanceType,
                                      forDate: String,
                                      metaData: List<AttendanceUnit>): EmployeeAttendanceResponse {
        return EmployeeAttendanceResponse(
            employee = employeeServiceConverter.getSavedEmployeeResponse(employee),
            workingHoursInMinutes = workingMinutes,
            attendanceType = attendanceType,
            forDate = forDate,
            metaData = metaData
        )
    }

    fun getAttendanceTypeAggregateResponse(attendanceType: AttendanceType, count: Int): AttendanceTypeAggregateResponse {
        return AttendanceTypeAggregateResponse(
            attendanceType = attendanceType,
            count = count
        )
    }

    fun getAttendanceInfoResponse(company: Company, forDate: String, employeesAttendance: List<EmployeeAttendanceResponse>, attendanceTypeAggregate: List<AttendanceTypeAggregateResponse>): AttendanceInfoResponse {
        return AttendanceInfoResponse(
            company = companyServiceConverter.getSavedCompanyResponse(company),
            forDate = forDate,
            employeesAttendance = employeesAttendance,
            attendanceTypeAggregate = attendanceTypeAggregate,
        )
    }

    private fun getMetaData(employee: Employee, overtimes: Map<Long?, List<Overtime>>, lateFines: Map<Long?, List<LateFine>>): List<AttendanceUnit> {
        val metaData = mutableListOf<AttendanceUnit>()
        overtimes.getOrDefault(employee.id, emptyList()).map {
            metaData.add(
                getAttendanceAggregateUnit(
                    attendanceType = AttendanceType.OVERTIME,
                    valueUnitType = ValueUnitType.MINUTE,
                    value = it.totalOvertimeMinutes.toString(),
                )
            )
        }
        lateFines.getOrDefault(employee.id, emptyList()).map {
            metaData.add(
                getAttendanceAggregateUnit(
                    attendanceType = AttendanceType.LATE_FINE,
                    valueUnitType = ValueUnitType.MINUTE,
                    value = it.totalLateFineMinutes.toString(),
                )
            )
        }
        return metaData
    }

    fun getAttendanceAggregateUnit(attendanceType: AttendanceType, valueUnitType: ValueUnitType, value: String): AttendanceUnit {
        return AttendanceUnit(
            attendanceType = attendanceType,
            valueUnitType = valueUnitType,
            value = value,
        )
    }

    private fun getAttendanceByAdminServerId(attendanceByAdminKey: AttendanceByAdminKey?): String {
        return attendanceByAdminKey?.companyId.toString() + "__" + attendanceByAdminKey?.employeeId.toString() + "__" + attendanceByAdminKey?.forDate;
    }

    fun getSavedAttendanceByAdminResponse(attendanceByAdmin: AttendanceByAdmin?): SavedAttendanceByAdminResponse? {
        return SavedAttendanceByAdminResponse(
            serverId = getAttendanceByAdminServerId(attendanceByAdmin?.id),
            employee = employeeServiceConverter.getSavedEmployeeResponse(attendanceByAdmin?.employee),
            company = companyServiceConverter.getSavedCompanyResponse(attendanceByAdmin?.company),
            forDate = attendanceByAdmin?.id?.forDate ?: "",
            attendanceType = attendanceByAdmin?.attendanceType ?: AttendanceType.NONE,
            addedBy = attendanceByAdmin?.addedBy?.id ?: "",
            workingMinutes = attendanceByAdmin?.workingMinutes ?: 0)
    }

    fun getAttendanceSummary(company: Company,
                             startTime: LocalDateTime,
                             endTime: LocalDateTime,
                             attendanceReportForEmployees: List<AttendanceReportForEmployee>): AttendanceSummaryResponse {
        return AttendanceSummaryResponse(
            company = companyServiceConverter.getSavedCompanyResponse(company),
            startTime = DateUtils.getEpoch(startTime),
            endTime = DateUtils.getEpoch(endTime),
            employeesReport = attendanceReportForEmployees.map {
                AttendanceReportForEmployeeResponse(
                    employee = employeeServiceConverter.getSavedEmployeeResponse(it.employee),
                    startTime = DateUtils.getEpoch(startTime),
                    endTime = DateUtils.getEpoch(endTime),
                    totalDay = it.totalDay,
                    presentDays = it.presentDays,
                    absentDays = it.absentDays,
                    halfDaysDays = it.halfDaysDays,
                    paidHolidays = it.paidHolidays,
                    nonPaidHolidays = it.nonPaidHolidays,
                    overtimeMinutes = it.overtimeMinutes,
                    overtimeAmountInPaisa = it.overtimeAmountInPaisa,
                    lateFineMinutes = it.lateFineMinutes,
                    lateFineAmountInPaisa = it.lateFineAmountInPaisa,
                    companyWorkingMinutesPerDay = it.companyWorkingMinutesPerDay
                )
            }
        )
    }
}
