package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.AttendanceType
import com.dukaankhata.server.enums.HolidayType
import com.dukaankhata.server.enums.PunchType
import com.dukaankhata.server.enums.SelfieType
import com.dukaankhata.server.utils.AttendanceUtils
import com.dukaankhata.server.utils.DateUtils
import com.dukaankhata.server.utils.HolidayUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Component
class AttendanceServiceConverter {

    private val offsetInMinuteForOvertime = 60//

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    @Autowired
    private lateinit var holidayUtils: HolidayUtils

    @Autowired
    private lateinit var attendanceUtils: AttendanceUtils

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

    fun getAttendanceInfo(company: Company, employeesForDate: List<Employee>, attendances: List<Attendance>, forDate: String): AttendanceInfoResponse? {
        val idsForAllEmployeesWithAttendanceMarked = mutableSetOf<Long>()
        val companyWorkingMinutes = company.workingMinutes
        val employeeAttendanceDetailsForDateResponse = attendances.groupBy { it.employee }.map { employeeAttendances ->
            employeeAttendances.key?.let { employee ->
                var totalWorkingMinute = 0
                var attendanceType: AttendanceType = AttendanceType.ABSENT
                // Attendance by Admin takes the highest priority
                val attendanceByAdmin = attendanceUtils.getAttendanceByAdmin(company, employee, forDate)
                if (attendanceByAdmin != null) {
                    attendanceType = attendanceByAdmin.attendanceType
                    totalWorkingMinute = attendanceByAdmin.workingMinutes
                } else {
                    val holiday = holidayUtils.getHoliday(company, employee, forDate)
                    if (holiday != null) {
                        attendanceType = if (holiday.holidayType == HolidayType.PAID) {
                            AttendanceType.HOLIDAY_PAID
                        } else {
                            AttendanceType.HOLIDAY_NON_PAID
                        }
                    } else {
                        // Calculate the attendance type based on actual Punch IN and OUT
                        val currentEmployeeAttendance = employeeAttendances.value
                        val allIns = currentEmployeeAttendance.filter { it.punchType == PunchType.IN }.sortedBy { it.punchAt }
                        val allOuts = currentEmployeeAttendance.filter { it.punchType == PunchType.OUT }.sortedBy { it.punchAt }

                        // Ins and Outs should be equal in count.
                        // Otherwise flag that attendance as error
                        when {
                            allIns.size > allOuts.size -> {
                                attendanceType = AttendanceType.OUT_NOT_MARKED
                            }
                            allOuts.size > allIns.size -> {
                                attendanceType = AttendanceType.IN_NOT_MARKED
                            }
                            else -> {
                                // Genuine case
                                // Evaluate
                                for (index in allIns.indices) {
                                    val inAttendance = allIns[index]
                                    val outAttendance = allOuts[index]
                                    val duration = Duration.between(outAttendance.punchAt, inAttendance.punchAt).abs()
                                    totalWorkingMinute += duration.toMinutes().toInt()
                                }
                                attendanceType = when {
                                    totalWorkingMinute == 0 -> {
                                        AttendanceType.ABSENT
                                    }
                                    totalWorkingMinute > companyWorkingMinutes + offsetInMinuteForOvertime -> {
                                        AttendanceType.OVERTIME
                                    }
                                    totalWorkingMinute < companyWorkingMinutes -> {
                                        AttendanceType.HALF_DAY
                                    }
                                    else -> {
                                        AttendanceType.PRESENT
                                    }
                                }
                            }
                        }
                    }
                }
                idsForAllEmployeesWithAttendanceMarked.add(employee.id)
                EmployeeAttendanceResponse(
                    employee = employeeServiceConverter.getSavedEmployeeResponse(employee),
                    workingHoursInMinutes = totalWorkingMinute,
                    attendanceType = attendanceType,
                    forDate = forDate
                )
            }
        }.filterNotNull()

        val idsForAllEmployeesForThatDate = employeesForDate.map { it.id }.toSet()

        val attendanceNotAvailableForEmployeesIds = idsForAllEmployeesForThatDate - idsForAllEmployeesWithAttendanceMarked

        val attendanceNotAvailableForEmployees = employeesForDate.filter { attendanceNotAvailableForEmployeesIds.contains(it.id) }

        val employeeAttendanceDetailsForDateResponseMutable = employeeAttendanceDetailsForDateResponse.toMutableList()

        attendanceNotAvailableForEmployees.map { employee ->
            var workingMinutes = 0
            var attendanceType = AttendanceType.ABSENT
            // Attendance by Admin takes the highest priority
            val attendanceByAdmin = attendanceUtils.getAttendanceByAdmin(company, employee, forDate)
            if (attendanceByAdmin != null) {
                attendanceType = attendanceByAdmin.attendanceType
                workingMinutes = attendanceByAdmin.workingMinutes
            } else {
                val holiday = holidayUtils.getHoliday(company, employee, forDate)
                if (holiday != null) {
                    attendanceType = if (holiday.holidayType == HolidayType.PAID) {
                        AttendanceType.HOLIDAY_PAID
                    } else {
                        AttendanceType.HOLIDAY_NON_PAID
                    }
                }
            }
            employeeAttendanceDetailsForDateResponseMutable.add(
                EmployeeAttendanceResponse(
                    employee = employeeServiceConverter.getSavedEmployeeResponse(employee),
                    workingHoursInMinutes = workingMinutes,
                    attendanceType = attendanceType,
                    forDate = forDate
                )
            )
        }

        val attendanceTypeAggregate = employeeAttendanceDetailsForDateResponseMutable.groupBy { it.attendanceType }.map {
            AttendanceTypeAggregateResponse(
                attendanceType = it.key,
                count = it.value.size
            )
        }
        return AttendanceInfoResponse(
            company = companyServiceConverter.getSavedCompanyResponse(company),
            forDate = forDate,
            employeesAttendance = employeeAttendanceDetailsForDateResponseMutable,
            attendanceTypeAggregate = attendanceTypeAggregate,
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

    fun getAttendanceSummary(requestedByUser: User, company: Company, attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse {
        TODO()
    }
}
