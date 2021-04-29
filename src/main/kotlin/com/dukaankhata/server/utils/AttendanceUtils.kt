package com.dukaankhata.server.utils

import AttendanceInfoData
import AttendancePunchData
import AttendanceReportForEmployee
import com.dukaankhata.server.dao.AttendanceByAdminRepository
import com.dukaankhata.server.dao.AttendanceRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.*
import com.dukaankhata.server.service.converter.AttendanceServiceConverter
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

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

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    fun saveAttendance(requestContext: RequestContext, saveAttendanceRequest: SaveAttendanceRequest): Attendance? {

        val employee = requestContext.employee ?: error("Employee is required")

        if (employee.salaryType == SalaryType.ONE_TIME) {
            logger.error("Can not add attendance for one time employee")
            return null
        }

        val attendance = Attendance()
        attendance.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.ATN.name)
        attendance.forDate = saveAttendanceRequest.forDate

        attendance.punchBy = requestContext.user
        attendance.punchAt = DateUtils.parseEpochInMilliseconds(saveAttendanceRequest.punchAt)
        attendance.punchType = saveAttendanceRequest.punchType

        attendance.selfieUrl = saveAttendanceRequest.selfieUrl
        attendance.selfieType = saveAttendanceRequest.selfieType

        attendance.locationLat = saveAttendanceRequest.locationLat
        attendance.locationLong = saveAttendanceRequest.locationLong
        attendance.locationName = saveAttendanceRequest.locationName

        attendance.employee = employee
        attendance.company = requestContext.company
        val savedAttendance = attendanceRepository.save(attendance)

        // Because for today, we will always cover that in the job that will run tomorrow
        if (savedAttendance.punchAt.toLocalDate().atStartOfDay().isBefore(DateUtils.dateTimeNow().toLocalDate().atStartOfDay())) {
            employeeUtils.updateSalary(employee, DateUtils.toStringDate(savedAttendance.punchAt))
        }

        return savedAttendance;
    }

    fun getAttendanceByAdminKey(companyId: String, employeeId: String, forDate: String): AttendanceByAdminKey {
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
        val company = requestContext.company ?: error("Company is required")
        val employee = requestContext.employee ?: error("Employee is required")

        if (employee.salaryType == SalaryType.ONE_TIME) {
            error("Can not mark attendance for one time employee")
        }

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
            attendanceByAdmin.lastModifiedAt = DateUtils.dateTimeNow()
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

    // Get attendance Summary for 1 month instead of 3 months.
    // Payment Summary gets summary for last 3 month.
    fun getAttendanceSummary(requestContext: RequestContext, attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse {
        val company = requestContext.company ?: error("Company is required")
        val reportDuration = DateUtils.getReportDuration(attendanceSummaryRequest.forYear, attendanceSummaryRequest.forMonth)

        return attendanceServiceConverter.getAttendanceSummary(
            company = company,
            startTime = reportDuration.startTime,
            endTime = reportDuration.endTime,
            attendanceReportForEmployees = getAttendanceReportForCompany(company, reportDuration.startTime, reportDuration.endTime)
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
                                                     overtimes: Map<String?, List<Overtime>>,
                                                     lateFines: Map<String?, List<LateFine>>): EmployeeAttendanceResponse {
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

    fun getEmployeeAttendanceAggregateReport(employee: Employee, forYear: Int, forMonth: Int): List<AttendanceTypeAggregateResponse> {
        val reportDuration = DateUtils.getReportDuration(forYear, forMonth)
        return getEmployeeAttendanceAggregateReport(employee, reportDuration.startTime, reportDuration.endTime)
    }

    fun getEmployeeAttendanceAggregateReport(employee: Employee, startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<AttendanceTypeAggregateResponse> {
        return runBlocking {
            val employeeAttendanceDetails = mutableListOf<EmployeeAttendanceResponse>()
            val datesList = DateUtils.getDatesBetweenInclusiveOfStartAndEndDates(startDateTime, endDateTime).map { DateUtils.toStringDate(it) }

            datesList.map { forDate ->
                employeeAttendanceDetails.addAll(getEmployeeAttendanceResponse(employee, forDate))
            }
            val aggregateIds = mutableMapOf<AttendanceType, Int>()
            employeeAttendanceDetails.map { emAtt ->
                aggregateIds[emAtt.attendanceType] = aggregateIds.getOrDefault(emAtt.attendanceType, 0) + 1
                emAtt.metaData.map { md ->
                    aggregateIds[md.attendanceType] = aggregateIds.getOrDefault(md.attendanceType, 0) + 1
                }
            }
            aggregateIds.map { attendanceServiceConverter.getAttendanceTypeAggregateResponse(it.key, it.value) }
        }
    }

    suspend fun getEmployeeAttendanceResponse(employee: Employee, forDate: String): List<EmployeeAttendanceResponse> {
        val employeeAttendanceDetails = mutableListOf<EmployeeAttendanceResponse>()
        val attendanceReportForEmployee = getAttendanceReportForEmployee(employee, forDate)
        attendanceReportForEmployee?.let { employeeReport ->
            if (employeeReport.presentDays > 0) {
                employeeAttendanceDetails.add(
                    attendanceServiceConverter.getEmployeeAttendanceResponse(
                        employee = employee,
                        workingMinutes = employeeReport.companyWorkingMinutesPerDay,
                        attendanceType = AttendanceType.PRESENT,
                        forDate = forDate,
                        metaData = getMetaData(employee, employeeReport)
                    )
                )
            }

            if (employeeReport.halfDays > 0) {
                employeeAttendanceDetails.add(
                    attendanceServiceConverter.getEmployeeAttendanceResponse(
                        employee = employee,
                        workingMinutes = employeeReport.companyWorkingMinutesPerDay/2,
                        attendanceType = AttendanceType.HALF_DAY,
                        forDate = forDate,
                        metaData = getMetaData(employee, employeeReport)
                    )
                )
            }

            if (employeeReport.absentDays > 0) {
                employeeAttendanceDetails.add(
                    attendanceServiceConverter.getEmployeeAttendanceResponse(
                        employee = employee,
                        workingMinutes = 0,
                        attendanceType = AttendanceType.ABSENT,
                        forDate = forDate,
                        metaData = getMetaData(employee, employeeReport)
                    )
                )
            }

            if (employeeReport.paidHolidays > 0) {
                employeeAttendanceDetails.add(
                    attendanceServiceConverter.getEmployeeAttendanceResponse(
                        employee = employee,
                        workingMinutes = 0,
                        attendanceType = AttendanceType.HOLIDAY_PAID,
                        forDate = forDate,
                        metaData = getMetaData(employee, employeeReport)
                    )
                )
            }

            if (employeeReport.nonPaidHolidays > 0) {
                employeeAttendanceDetails.add(
                    attendanceServiceConverter.getEmployeeAttendanceResponse(
                        employee = employee,
                        workingMinutes = 0,
                        attendanceType = AttendanceType.HOLIDAY_NON_PAID,
                        forDate = forDate,
                        metaData = getMetaData(employee, employeeReport)
                    )
                )
            }
        }
        return employeeAttendanceDetails
    }

    suspend fun getAttendanceInfoV2(company: Company, forDate: String): AttendanceInfoResponse? {
        return coroutineScope {

            val dateToBeUsed = DateUtils.parseStandardDate(forDate)
//            val startDateTime = dateToBeUsed.toLocalDate().atStartOfDay()
            val endDateTime = dateToBeUsed.toLocalDate().atTime(LocalTime.MAX)

            val employeeAttendanceDetails = mutableListOf<EmployeeAttendanceResponse>()

            employeeUtils.getEmployees(company, endDateTime)
                .filterNot { it.salaryType == SalaryType.ONE_TIME }
                .map { employee ->
                    async {
                        logger.debug("Get attendance for employee: ${employee.id}")
                        employeeAttendanceDetails.addAll(getEmployeeAttendanceResponse(employee, forDate))
                    }
                }.map {
                    it.await()
                }

            val aggregateIds = mutableMapOf<AttendanceType, MutableSet<String>>()
            employeeAttendanceDetails.map { emAtt ->
                aggregateIds[emAtt.attendanceType] = (aggregateIds.getOrDefault(emAtt.attendanceType, emptySet()) + setOf(emAtt.employee.serverId)).toMutableSet()
                emAtt.metaData.map { md ->
                    aggregateIds[md.attendanceType] = (aggregateIds.getOrDefault(md.attendanceType, emptySet()) + setOf(emAtt.employee.serverId)).toMutableSet()
                }
            }

            val attendanceTypeAggregate = aggregateIds.map { attendanceServiceConverter.getAttendanceTypeAggregateResponse(it.key, it.value.size) }
            attendanceServiceConverter.getAttendanceInfoResponse(company, forDate, employeeAttendanceDetails.sortedBy { it.employee.salaryType.value }, attendanceTypeAggregate)
        }
    }

    private fun getMetaData(employee: Employee, attendanceReportForEmployee: AttendanceReportForEmployee): List<AttendanceUnit> {
        val metaData = mutableListOf<AttendanceUnit>()
        if (attendanceReportForEmployee.overtimeMinutes > 0) {
            metaData.add(
                AttendanceUnit(
                    attendanceType = AttendanceType.OVERTIME,
                    valueUnitType = ValueUnitType.MINUTE,
                    value = attendanceReportForEmployee.overtimeMinutes.toString(),
                )
            )
        }

        if (attendanceReportForEmployee.lateFineMinutes > 0) {
            metaData.add(
                AttendanceUnit(
                    attendanceType = AttendanceType.LATE_FINE,
                    valueUnitType = ValueUnitType.MINUTE,
                    value = attendanceReportForEmployee.lateFineMinutes.toString(),
                )
            )
        }
        return metaData
    }

    suspend fun getAttendanceInfo(company: Company, forDate: String): AttendanceInfoResponse? {
        return withContext(Dispatchers.Default) {
            val idsForAllEmployeesWithAttendanceMarked = mutableSetOf<Long>()
            val companyWorkingMinutes = company.workingMinutes

            val punchedAttendances = async { findByCompanyAndForDate(company, forDate) }
            // get all employees who were on payroll that day
            val employeesForDate = employeeUtils.getEmployees(company, DateUtils.parseStandardDate(forDate)).filterNot { it.salaryType == SalaryType.ONE_TIME }

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
            val idsForAllEmployeesForThatDate = employeesForDate.map { it.id }.toSet()
            val attendanceNotAvailableForEmployeesIds = idsForAllEmployeesForThatDate - idsForAllEmployeesWithAttendanceMarked
            val attendanceNotAvailableForEmployees = async { employeesForDate.filter { attendanceNotAvailableForEmployeesIds.contains(it.id) } }
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

    private fun getMetaData(employee: Employee, overtimes: Map<String?, List<Overtime>>, lateFines: Map<String?, List<LateFine>>): List<AttendanceUnit> {
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
                                          datesList: List<String>): List<Attendance> {
        return try {
            attendanceRepository.getAttendancesForEmployee(employee.id, datesList)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAttendancesByAdminForEmployee(employee: Employee,
                                                 datesList: List<String>): List<AttendanceByAdmin> {
        return try {
            attendanceByAdminRepository.getAttendancesByAdminForEmployee(employee.id, datesList)
        } catch (e: Exception) {
            emptyList()
        }
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
                                       forDate: String): AttendanceReportForEmployee? {
        val dateToBeUsed = DateUtils.parseStandardDate(forDate)
        val startDateTime = dateToBeUsed.toLocalDate().atStartOfDay()
        val endDateTime = dateToBeUsed.toLocalDate().atTime(LocalTime.MAX)
        return getAttendanceReportForEmployee(employee, startDateTime, endDateTime)
    }

    fun getAttendanceReportForEmployee(employee: Employee,
                                       startTime: LocalDateTime,
                                       endTime: LocalDateTime): AttendanceReportForEmployee? {
        return runBlocking {
            if (employee.salaryType == SalaryType.ONE_TIME) {
                logger.error("Can not get report for one time employee")
                return@runBlocking null
            }
            val company = employee.company ?: error("Missing company for the employee")
            val companyWorkingMinutes = company.workingMinutes

            val startDate = DateUtils.toStringDate(startTime)
            val endDate = DateUtils.toStringDate(endTime)

            val datesList = DateUtils.getDatesBetweenInclusiveOfStartAndEndDates(startTime, endTime).map { DateUtils.toStringDate(it) }

            val punchedAttendances = getAttendancesForEmployee(employee, datesList).groupBy { it.forDate }
            val attendancesByAdmin = getAttendancesByAdminForEmployee(employee, datesList).groupBy { it.id!!.forDate }
            val holidays = holidayUtils.getHolidayForEmployee(employee, datesList).groupBy { it.id!!.forDate }
            val overtimes = overtimeUtils.getOvertimesForEmployee(employee, datesList).groupBy { it.forDate }
            val lateFines = lateFineUtils.getLateFinesForEmployee(employee, datesList).groupBy { it.forDate }

            val allAttendanceTypes = mutableListOf<AttendanceType>()
            datesList.map { forDate ->
//                val forDate = DateUtils.toStringDate(it)
                var attendanceType: AttendanceType = AttendanceType.ABSENT

                val punchedAttendancesForDate = punchedAttendances.getOrDefault(forDate, emptyList())
                // Should have max 1 entry
                val attendancesByAdminForDate = attendancesByAdmin.getOrDefault(forDate, emptyList())
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
            val halfDays = groupedAttendance.getOrDefault(AttendanceType.HALF_DAY, emptyList()).size
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
                halfDays = halfDays,
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
            }.mapNotNull {
                it.await()
            }
        }
    }

    fun getAttendanceSummaryForEmployee(employee: Employee, forYear: Int, forMonth: Int): Map<String, AttendanceInfoData> {
        val reportDuration = DateUtils.getReportDuration(forYear, forMonth)
        val datesList = DateUtils.getDatesBetweenInclusiveOfStartAndEndDates(reportDuration.startTime, reportDuration.endTime).map { DateUtils.toStringDate(it) }
        val attendancesReport = mutableMapOf<String, AttendanceInfoData>()
        val company = employee.company ?: error("Company should always be present")
        datesList.map { forDate ->
            val attendanceReportForEmployee = getAttendanceReportForEmployee(employee, forDate)
            attendanceReportForEmployee?.let { employeeReport ->
                when {
                    employeeReport.presentDays > 0 -> {
                        attendancesReport[forDate] = AttendanceInfoData(
                            attendanceType = AttendanceType.PRESENT,
                            displayText = DateUtils.getMinutesToHourString(company.workingMinutes) + " Hrs",
                            dateNumber = DateUtils.getDateNumber(forDate),
                            dateText = DateUtils.getWeekName(forDate))
                    }
                    employeeReport.halfDays > 0 -> {
                        attendancesReport[forDate] = AttendanceInfoData(
                            attendanceType = AttendanceType.HALF_DAY,
                            displayText = DateUtils.getMinutesToHourString(company.workingMinutes/2) + " Hrs",
                            dateNumber = DateUtils.getDateNumber(forDate),
                            dateText = DateUtils.getWeekName(forDate))
                    }
                    employeeReport.absentDays > 0 -> {
                        attendancesReport[forDate] = AttendanceInfoData(
                            attendanceType = AttendanceType.ABSENT,
                            displayText = "0:00 Hrs",
                            dateNumber = DateUtils.getDateNumber(forDate),
                            dateText = DateUtils.getWeekName(forDate))
                    }
                    employeeReport.paidHolidays > 0 -> {
                        attendancesReport[forDate] = AttendanceInfoData(
                            attendanceType = AttendanceType.HOLIDAY_PAID,
                            displayText = "On Paid Holiday",
                            dateNumber = DateUtils.getDateNumber(forDate),
                            dateText = DateUtils.getWeekName(forDate))
                    }
                    employeeReport.nonPaidHolidays > 0 -> {
                        attendancesReport[forDate] = AttendanceInfoData(
                            attendanceType = AttendanceType.HOLIDAY_NON_PAID,
                            displayText = "On Un-Paid Holiday",
                            dateNumber = DateUtils.getDateNumber(forDate),
                            dateText = DateUtils.getWeekName(forDate))
                    }
                    else -> {
                        attendancesReport[forDate] = AttendanceInfoData(
                            attendanceType = AttendanceType.NONE,
                            displayText = "Unknown",
                            dateNumber = 7,
                            dateText = DateUtils.getWeekName(forDate))
                    }
                }
            }
        }
        return attendancesReport
    }
}
