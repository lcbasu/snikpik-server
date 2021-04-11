package com.dukaankhata.server.utils

import AttendancePunchData
import AttendanceReportForEmployee
import com.dukaankhata.server.dao.AttendanceByAdminRepository
import com.dukaankhata.server.dao.AttendanceRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.AttendanceType
import com.dukaankhata.server.enums.HolidayType
import com.dukaankhata.server.enums.PunchType
import com.dukaankhata.server.enums.ValueUnitType
import com.dukaankhata.server.service.converter.AttendanceServiceConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Component
class AttendanceUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val offsetInMinuteForOvertime = 60//

    @Autowired
    private lateinit var attendanceRepository: AttendanceRepository

    @Autowired
    private lateinit var attendanceByAdminRepository: AttendanceByAdminRepository

    @Autowired
    private lateinit var holidayUtils: HolidayUtils

    @Autowired
    private lateinit var overtimeUtils: OvertimeUtils

    @Autowired
    private lateinit var lateFineUtils: LateFineUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var attendanceServiceConverter: AttendanceServiceConverter

    fun saveAttendance(requestContext: RequestContext, saveAttendanceRequest: SaveAttendanceRequest): Attendance? {
        val attendance = Attendance()

        attendance.forDate = saveAttendanceRequest.forDate

        attendance.punchBy = requestContext.user
        attendance.punchAt = DateUtils.parseEpochInMilliseconds(saveAttendanceRequest.punchAt)
        attendance.punchType = saveAttendanceRequest.punchType

        attendance.selfieUrl = saveAttendanceRequest.selfieUrl
        attendance.selfieType = saveAttendanceRequest.selfieType

        attendance.locationLat = saveAttendanceRequest.locationLat
        attendance.locationLong = saveAttendanceRequest.locationLong
        attendance.locationName = saveAttendanceRequest.locationName

        attendance.employee = requestContext.employee
        attendance.company = requestContext.company
        val savedAttendance = attendanceRepository.save(attendance)

        // Because for today, we will always cover that in the job that will run tomorrow
        if (savedAttendance.punchAt.toLocalDate().atStartOfDay().isBefore(DateUtils.dateTimeNow().toLocalDate().atStartOfDay())) {
            employeeUtils.updateSalary(attendance.employee!!, DateUtils.toStringDate(savedAttendance.punchAt))
        }

        return savedAttendance;
    }

    fun getAttendanceByAdminKey(companyId: Long, employeeId: Long, forDate: String): AttendanceByAdminKey {
        val key = AttendanceByAdminKey()
        key.companyId = companyId
        key.employeeId = employeeId
        key.forDate = forDate
        return key
    }

    suspend fun getAttendanceByAdminForDate(company: Company, forDate: String): List<AttendanceByAdmin> =
        try {
            attendanceByAdminRepository.getAllAttendancesByAdminForDate(
                companyId = company.id,
                forDate = forDate
            )
        } catch (e: Exception) {
            emptyList()
        }

    fun getAttendanceByAdmin(company: Company, employee: Employee, forDate: String): AttendanceByAdmin? =
        try {
            val key = getAttendanceByAdminKey(companyId = company.id, employeeId = employee.id, forDate = forDate)
            attendanceByAdminRepository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun markAttendance(requestContext: RequestContext, markAttendanceRequest: MarkAttendanceRequest): AttendanceByAdmin {
        val addedByUser = requestContext.user
        val company = requestContext.company!!
        val employee = requestContext.employee!!
        val workingMinutes = when (markAttendanceRequest.attendanceType) {
            AttendanceType.PRESENT -> company.workingMinutes
            AttendanceType.HALF_DAY -> company.workingMinutes / 2
            else -> 0
        }

        val key = getAttendanceByAdminKey(companyId = company.id, employeeId = employee.id, forDate = markAttendanceRequest.forDate)
        val attendanceByAdmin = getAttendanceByAdmin(company, employee, markAttendanceRequest.forDate)

        val savedAttendance = if (attendanceByAdmin != null) {
            // Already present so only update
            logger.debug("AttendanceByAdmin Already present so only update")
            attendanceByAdmin.addedBy = addedByUser
            attendanceByAdmin.workingMinutes = workingMinutes
            attendanceByAdmin.attendanceType = markAttendanceRequest.attendanceType
            attendanceByAdminRepository.save(attendanceByAdmin)
        } else {
            // Save new one
            logger.debug("Save new AttendanceByAdmin")
            val newAttendance = AttendanceByAdmin()
            newAttendance.id = getAttendanceByAdminKey(companyId = company.id, employeeId = employee.id, forDate = markAttendanceRequest.forDate)
            newAttendance.attendanceType = markAttendanceRequest.attendanceType
            newAttendance.workingMinutes = workingMinutes
            newAttendance.employee = employee
            newAttendance.company = company
            newAttendance.addedBy = addedByUser
            attendanceByAdminRepository.save(newAttendance)
        }

        // Because for today, we will always cover that in the job that will run tomorrow
        if (DateUtils.parseStandardDate(markAttendanceRequest.forDate).toLocalDate().atStartOfDay().isBefore(DateUtils.dateTimeNow().toLocalDate().atStartOfDay())) {
            employeeUtils.updateSalary(employee, markAttendanceRequest.forDate)
        }

        return savedAttendance
    }

    fun getAttendanceSummary(requestContext: RequestContext, attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse {
        val company = requestContext.company!!

        // Choosing a random date in middle to select correct start and end month
        val startDate = LocalDateTime.of(attendanceSummaryRequest.forYear, attendanceSummaryRequest.forMonth, 20, 0, 0, 0, 0)
        val startTime = startDate.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay().minusMonths(2) // get data from past 2 months
        val endTime = startDate.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX)

        return attendanceServiceConverter.getAttendanceSummary(
            company = company,
            startTime = startTime,
            endTime = endTime,
            attendanceReportForEmployees = getAttendanceReportForCompany(company, startTime, endTime)
        )
    }

    fun getAttendanceByCompanyAndDates(company: Company, forDates: Set<String>): List<Attendance> {
        return attendanceRepository.getAttendanceByCompanyAndDates(company, forDates)
    }

    suspend fun findByCompanyAndForDate(company: Company, forDate: String): List<Attendance> {
        return attendanceRepository.findByCompanyAndForDate(company, forDate)
    }

    fun getEmployeeAttendanceDataForPunchedEmployees(employee: Employee,
                                                     currentEmployeeAttendanceForDate: List<Attendance>,
                                                     companyWorkingMinutes: Int,
                                                     forDate: String,
                                                     attendancesByAdminForDate: List<AttendanceByAdmin>,
                                                     holidayForDate: List<Holiday>,
                                                     overtimes: Map<Long?, List<Overtime>>,
                                                     lateFines: Map<Long?, List<LateFine>>): EmployeeAttendanceResponse {
        var totalWorkingMinute = 0
        var attendanceType: AttendanceType = AttendanceType.ABSENT
        // Attendance by Admin takes the highest priority
        val attendanceByAdmin = attendancesByAdminForDate.findLast { it.employee?.id == employee.id }
        if (attendanceByAdmin != null) {
            attendanceType = attendanceByAdmin.attendanceType
            totalWorkingMinute = attendanceByAdmin.workingMinutes
        } else {
            val holiday = holidayForDate.findLast { it.employee?.id == employee.id }
            if (holiday != null) {
                attendanceType = if (holiday.holidayType == HolidayType.PAID) {
                    AttendanceType.HOLIDAY_PAID
                } else {
                    AttendanceType.HOLIDAY_NON_PAID
                }
            } else {
                getAttendanceInfoFromPunchedDataForDate(currentEmployeeAttendanceForDate, companyWorkingMinutes)?.let {
                    attendanceType = it.attendanceType
                    totalWorkingMinute = it.totalMinutes
                }
            }
        }
        return attendanceServiceConverter.getEmployeeAttendanceResponse(
            employee = employee,
            workingMinutes = totalWorkingMinute,
            attendanceType = attendanceType,
            forDate = forDate,
            metaData = getMetaData(employee, overtimes, lateFines)
        )
    }

    fun getAttendanceInfoFromPunchedDataForDate(punchedAttendanceForDate: List<Attendance>,
                                                companyWorkingMinutes: Int): AttendancePunchData? {

        if (punchedAttendanceForDate.isEmpty()) {
            return null
        }

        var lastUpdatedAt: LocalDateTime = DateUtils.parseEpochInMilliseconds(0) // Deliberately keeping this to 0 if the attendance is not proper
        var totalWorkingMinute = 0
        var attendanceType: AttendanceType = AttendanceType.ABSENT
        val allIns = punchedAttendanceForDate.filter { it.punchType == PunchType.IN }.sortedBy { it.punchAt }
        val allOuts = punchedAttendanceForDate.filter { it.punchType == PunchType.OUT }.sortedBy { it.punchAt }

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

                    if (lastUpdatedAt == null || outAttendance.punchAt.isAfter(lastUpdatedAt)) {
                        lastUpdatedAt = outAttendance.punchAt
                    }
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
        return AttendancePunchData(
            attendanceType = attendanceType,
            totalMinutes = totalWorkingMinute,
            updatedAt = lastUpdatedAt
        )
    }

    suspend fun getAttendanceInfo(company: Company, forDate: String): AttendanceInfoResponse? {
        return withContext(Dispatchers.Default) {
            val idsForAllEmployeesWithAttendanceMarked = mutableSetOf<Long>()
            val companyWorkingMinutes = company.workingMinutes

            val punchedAttendances = async { findByCompanyAndForDate(company, forDate) }
            // get all employees who were on payroll that day
            val employeesForDate = async { employeeUtils.getEmployees(company, DateUtils.parseStandardDate(forDate)) }

            val attendancesByAdminForDate = async {
                logger.debug("async for attendancesByAdminForDate")
                getAttendanceByAdminForDate(company, forDate)
            }
            val holidayForDate = async {
                logger.debug("async for holidayForDate")
                holidayUtils.getHolidayForDate(company, forDate)
            }

            val overtimes = async {
                logger.debug("async for overtimes")
                overtimeUtils.getAllOvertimesForDate(company, forDate).filterNot { it.employee == null }.groupBy { it.employee?.id } }

            val lateFines = async {
                logger.debug("async for lateFines")
                lateFineUtils.getAllLateFineForDate(company, forDate).filterNot { it.employee == null }.groupBy { it.employee?.id } }

            // For punch in employees
            val employeeAttendanceDetailsForDateResponse = async {
                logger.debug("async for employeeAttendanceDetailsForDateResponse")
                punchedAttendances.await().groupBy { it.employee }.map { employeeAttendances ->
                    employeeAttendances.key?.let { employee ->
                        val em = getEmployeeAttendanceDataForPunchedEmployees(
                            employee,
                            employeeAttendances.value,
                            companyWorkingMinutes,
                            forDate,
                            attendancesByAdminForDate.await(),
                            holidayForDate.await(),
                            overtimes.await(),
                            lateFines.await()
                        )
                        idsForAllEmployeesWithAttendanceMarked.add(em.employee.serverId.toLong())
                        em
                    }
                }.filterNotNull()
            }

            // For other than punched in
            val idsForAllEmployeesForThatDate = employeesForDate.await().map { it.id }.toSet()
            val attendanceNotAvailableForEmployeesIds = idsForAllEmployeesForThatDate - idsForAllEmployeesWithAttendanceMarked
            val attendanceNotAvailableForEmployees = async { employeesForDate.await().filter { attendanceNotAvailableForEmployeesIds.contains(it.id) } }
            val employeeAttendanceDetailsForDateResponseMutable = employeeAttendanceDetailsForDateResponse.await().toMutableList()
            attendanceNotAvailableForEmployees.await().map { employee ->
                var workingMinutes = 0
                var attendanceType = AttendanceType.ABSENT
                // Attendance by Admin takes the highest priority
                val attendanceByAdmin = attendancesByAdminForDate.await().findLast { it.employee?.id == employee.id }
                if (attendanceByAdmin != null) {
                    attendanceType = attendanceByAdmin.attendanceType
                    workingMinutes = attendanceByAdmin.workingMinutes
                } else {
                    val holiday = holidayForDate.await().findLast { it.employee?.id == employee.id }
                    if (holiday != null) {
                        attendanceType = if (holiday.holidayType == HolidayType.PAID) {
                            AttendanceType.HOLIDAY_PAID
                        } else {
                            AttendanceType.HOLIDAY_NON_PAID
                        }
                    }
                }
                employeeAttendanceDetailsForDateResponseMutable.add(
                    attendanceServiceConverter.getEmployeeAttendanceResponse(
                        employee = employee,
                        workingMinutes = workingMinutes,
                        attendanceType = attendanceType,
                        forDate = forDate,
                        metaData = getMetaData(employee, overtimes.await(), lateFines.await())
                    )
                )
            }

            val aggregateIds = mutableMapOf<AttendanceType, MutableSet<String>>()
            employeeAttendanceDetailsForDateResponseMutable.map { emAtt ->
                aggregateIds[emAtt.attendanceType] = (aggregateIds.getOrDefault(emAtt.attendanceType, emptySet()) + setOf(emAtt.employee.serverId)).toMutableSet()
                emAtt.metaData.map { md ->
                    aggregateIds[md.attendanceType] = (aggregateIds.getOrDefault(md.attendanceType, emptySet()) + setOf(emAtt.employee.serverId)).toMutableSet()
                }
            }

            val attendanceTypeAggregate = aggregateIds.map { attendanceServiceConverter.getAttendanceTypeAggregateResponse(it.key, it.value.size) }
            attendanceServiceConverter.getAttendanceInfoResponse(company, forDate, employeeAttendanceDetailsForDateResponseMutable, attendanceTypeAggregate)
        }
    }

    private fun getMetaData(employee: Employee, overtimes: Map<Long?, List<Overtime>>, lateFines: Map<Long?, List<LateFine>>): List<AttendanceUnit> {
        val metaData = mutableListOf<AttendanceUnit>()
        overtimes.getOrDefault(employee.id, emptyList()).map {
            metaData.add(
                AttendanceUnit(
                    attendanceType = AttendanceType.OVERTIME,
                    valueUnitType = ValueUnitType.MINUTE,
                    value = it.totalOvertimeMinutes.toString(),
                )
            )
        }
        lateFines.getOrDefault(employee.id, emptyList()).map {
            metaData.add(
                AttendanceUnit(
                    attendanceType = AttendanceType.LATE_FINE,
                    valueUnitType = ValueUnitType.MINUTE,
                    value = it.totalLateFineMinutes.toString(),
                )
            )
        }
        return metaData
    }


    suspend fun getAttendancesForEmployee(employee: Employee,
                                          startTime: LocalDateTime,
                                          endTime: LocalDateTime): List<Attendance> {
        return try {
            attendanceRepository.getAttendancesForEmployee(employee.id, startTime, endTime)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAttendancesByAdminForEmployee(employee: Employee,
                                                 startTime: LocalDateTime,
                                                 endTime: LocalDateTime): List<AttendanceByAdmin> {
        return try {
            attendanceByAdminRepository.getAttendancesByAdminForEmployee(employee.id, startTime, endTime)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAttendanceReportForEmployee(employee: Employee,
                                       startTime: LocalDateTime,
                                       endTime: LocalDateTime): AttendanceReportForEmployee {
        return runBlocking {
            val company = employee.company ?: error("Missing company for the employee")
            val companyWorkingMinutes = company.workingMinutes

            val startDate = DateUtils.toStringDate(startTime)
            val endDate = DateUtils.toStringDate(endTime)

            val punchedAttendances = async { getAttendancesForEmployee(employee, startTime, endTime).groupBy { it.forDate } }
            val attendancesByAdmin = async { getAttendancesByAdminForEmployee(employee, startTime, endTime).groupBy { it.id!!.forDate }  }
            val holidays = holidayUtils.getHolidayForEmployee(employee, startTime, endTime).groupBy { it.id!!.forDate }
            val overtimes = overtimeUtils.getOvertimesForEmployee(employee, startTime, endTime).groupBy { it.forDate }
            val lateFines = lateFineUtils.getLateFinesForEmployee(employee, startTime, endTime).groupBy { it.forDate }

            val allAttendanceTypes = mutableListOf<AttendanceType>()
            DateUtils.getDatesBetweenInclusiveOfStartAndEndDates(startTime, endTime).map {
                val forDate = DateUtils.toStringDate(it)
                var attendanceType: AttendanceType = AttendanceType.ABSENT

                val punchedAttendancesForDate = punchedAttendances.await().getOrDefault(forDate, emptyList())
                // Should have max 1 entry
                val attendancesByAdminForDate = attendancesByAdmin.await().getOrDefault(forDate, emptyList())
                // Should have max 1 entry
                val holidaysForDate = holidays.getOrDefault(forDate, emptyList())

                val punchAttendanceData = getAttendanceInfoFromPunchedDataForDate(punchedAttendancesForDate, companyWorkingMinutes)
                val attendanceByAdmin = attendancesByAdminForDate.firstOrNull()
                val holiday = holidaysForDate.firstOrNull()

                if (punchAttendanceData != null) {
                    // Then we need to find attendance in consideration with attendanceByAdmin and holiday
                    if (attendanceByAdmin != null && holiday != null) {
                        if (attendanceByAdmin.lastModifiedAt.isAfter(punchAttendanceData.updatedAt) && attendanceByAdmin.lastModifiedAt.isAfter(holiday.lastModifiedAt)) {
                            attendanceType = attendanceByAdmin.attendanceType
                        } else if (holiday.lastModifiedAt.isAfter(punchAttendanceData.updatedAt) && holiday.lastModifiedAt.isAfter(attendanceByAdmin.lastModifiedAt)) {
                            when (holiday.holidayType) {
                                HolidayType.PAID -> attendanceType = AttendanceType.HOLIDAY_PAID
                                HolidayType.NON_PAID -> attendanceType = AttendanceType.HOLIDAY_NON_PAID
                                HolidayType.NONE -> attendanceType = AttendanceType.HOLIDAY_NON_PAID
                            }
                        } else {
                            attendanceType = punchAttendanceData.attendanceType
                        }
                    } else if (attendanceByAdmin != null) {
                        if (attendanceByAdmin.lastModifiedAt.isAfter(punchAttendanceData.updatedAt)) {
                            attendanceType = attendanceByAdmin.attendanceType
                        } else {
                            attendanceType = punchAttendanceData.attendanceType
                        }
                    } else if (holiday != null) {
                        if (holiday.lastModifiedAt.isAfter(punchAttendanceData.updatedAt)) {
                            when (holiday.holidayType) {
                                HolidayType.PAID -> attendanceType = AttendanceType.HOLIDAY_PAID
                                HolidayType.NON_PAID -> attendanceType = AttendanceType.HOLIDAY_NON_PAID
                                HolidayType.NONE -> attendanceType = AttendanceType.HOLIDAY_NON_PAID
                            }
                        } else {
                            attendanceType = punchAttendanceData.attendanceType
                        }
                    } else {
                        // No holiday or attendance by Admin
                        attendanceType = punchAttendanceData.attendanceType
                    }
                } else if (attendanceByAdmin != null && holiday != null) {
                    if (attendanceByAdmin.lastModifiedAt.isAfter(holiday.lastModifiedAt)) {
                        attendanceType = attendanceByAdmin.attendanceType
                    } else {
                        when (holiday.holidayType) {
                            HolidayType.PAID -> attendanceType = AttendanceType.HOLIDAY_PAID
                            HolidayType.NON_PAID -> attendanceType = AttendanceType.HOLIDAY_NON_PAID
                            HolidayType.NONE -> attendanceType = AttendanceType.HOLIDAY_NON_PAID
                        }
                    }
                } else if (attendanceByAdmin != null) {
                    attendanceType = attendanceByAdmin.attendanceType
                } else if (holiday != null) {
                    when (holiday.holidayType) {
                        HolidayType.PAID -> attendanceType = AttendanceType.HOLIDAY_PAID
                        HolidayType.NON_PAID -> attendanceType = AttendanceType.HOLIDAY_NON_PAID
                        HolidayType.NONE -> attendanceType = AttendanceType.HOLIDAY_NON_PAID
                    }
                } else {
                    // No holiday or attendance by Admin
                    logger.error("This only happens when no one marked the attendance")
                    attendanceType = AttendanceType.ABSENT
                }
                allAttendanceTypes.add(attendanceType)

                // This can be used to print daily summary on the PDF report
//                val overtimesForDate = overtimes.await().getOrDefault(forDate, emptyList())
//                val lateFinesForDate = lateFines.await().getOrDefault(forDate, emptyList())
            }

            val groupedAttendance = allAttendanceTypes.groupBy { it }

            val totalDay = allAttendanceTypes.size

            // Overtime is also a marker for present
            val presentDays = groupedAttendance.getOrDefault(AttendanceType.PRESENT, emptyList()).size +
                groupedAttendance.getOrDefault(AttendanceType.OVERTIME, emptyList()).size
            val absentDays = groupedAttendance.getOrDefault(AttendanceType.ABSENT, emptyList()).size
            val halfDaysDays = groupedAttendance.getOrDefault(AttendanceType.HALF_DAY, emptyList()).size
            val paidHolidays = groupedAttendance.getOrDefault(AttendanceType.HOLIDAY_PAID, emptyList()).size
            val nonPaidHolidays = groupedAttendance.getOrDefault(AttendanceType.HOLIDAY_NON_PAID, emptyList()).size

            var overtimeMinutes = 0
            var overtimeAmountInPaisa = 0L
            var lateFineMinutes = 0
            var lateFineAmountInPaisa = 0L

            overtimes.map {
                it.value.map {
                    overtimeMinutes += it.totalOvertimeMinutes
                    overtimeAmountInPaisa += it.totalOvertimeAmountInPaisa
                }
            }
            lateFines.map {
                it.value.map {
                    lateFineMinutes += it.totalLateFineMinutes
                    lateFineAmountInPaisa += it.totalLateFineAmountInPaisa
                }
            }
            AttendanceReportForEmployee (
                employee = employee,
                startDate = startDate,
                endDate = endDate,
                totalDay = totalDay,
                presentDays = presentDays,
                absentDays = absentDays,
                halfDaysDays = halfDaysDays,
                paidHolidays = paidHolidays,
                nonPaidHolidays = nonPaidHolidays,
                overtimeMinutes = overtimeMinutes,
                overtimeAmountInPaisa = overtimeAmountInPaisa,
                lateFineMinutes = lateFineMinutes,
                lateFineAmountInPaisa = lateFineAmountInPaisa,
                companyWorkingMinutesPerDay = companyWorkingMinutes
            )
        }
    }

    fun getAttendanceReportForCompany(company: Company,
                                      startTime: LocalDateTime,
                                      endTime: LocalDateTime): List<AttendanceReportForEmployee> {
        return runBlocking {
            employeeUtils.getEmployees(company, endTime).map {
                async {
                    logger.debug("Get attendance for employee: ${it.id}")
                    getAttendanceReportForEmployee(it, startTime, endTime)
                }
            }.map {
                it.await()
            }
        }
    }
}
