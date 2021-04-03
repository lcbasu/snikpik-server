package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.*
import com.dukaankhata.server.dto.AttendanceSummaryRequest
import com.dukaankhata.server.dto.AttendanceSummaryResponse
import com.dukaankhata.server.dto.MarkAttendanceRequest
import com.dukaankhata.server.dto.SaveAttendanceRequest
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.AttendanceType
import com.dukaankhata.server.service.converter.AttendanceServiceConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Component
class AttendanceUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var attendanceRepository: AttendanceRepository

    @Autowired
    private lateinit var attendanceByAdminRepository: AttendanceByAdminRepository

    @Autowired
    private lateinit var holidayRepository: HolidayRepository

    @Autowired
    private lateinit var overtimeRepository: OvertimeRepository

    @Autowired
    private lateinit var lateFineRepository: LateFineRepository

    @Autowired
    private lateinit var attendanceServiceConverter: AttendanceServiceConverter

    fun saveAttendance(punchByUser: User, company: Company, employee: Employee, saveAttendanceRequest: SaveAttendanceRequest): Attendance? =
        attendanceRepository.let {
            val newAttendance = Attendance()

            newAttendance.forDate = saveAttendanceRequest.forDate

            newAttendance.punchBy = punchByUser
            newAttendance.punchAt = DateUtils.parseEpochInMilliseconds(saveAttendanceRequest.punchAt)
            newAttendance.punchType = saveAttendanceRequest.punchType

            newAttendance.selfieUrl = saveAttendanceRequest.selfieUrl
            newAttendance.selfieType = saveAttendanceRequest.selfieType

            newAttendance.locationLat = saveAttendanceRequest.locationLat
            newAttendance.locationLong = saveAttendanceRequest.locationLong
            newAttendance.locationName = saveAttendanceRequest.locationName

            newAttendance.employee = employee
            newAttendance.company = company

            it.save(newAttendance)
        }

    fun getAttendanceByAdminKey(companyId: Long, employeeId: Long, forDate: String): AttendanceByAdminKey {
        val key = AttendanceByAdminKey()
        key.companyId = companyId
        key.employeeId = employeeId
        key.forDate = forDate
        return key
    }

    fun getAttendanceByAdmin(company: Company, employee: Employee, forDate: String): AttendanceByAdmin? =
        try {
            val key = getAttendanceByAdminKey(companyId = company.id, employeeId = employee.id, forDate = forDate)
            attendanceByAdminRepository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun markAttendance(addedByUser: User, company: Company, employee: Employee, markAttendanceRequest: MarkAttendanceRequest): AttendanceByAdmin {
        val workingMinutes = when (markAttendanceRequest.attendanceType) {
            AttendanceType.PRESENT -> company.workingMinutes
            AttendanceType.HALF_DAY -> company.workingMinutes / 2
            else -> 0
        }

        val key = getAttendanceByAdminKey(companyId = company.id, employeeId = employee.id, forDate = markAttendanceRequest.forDate)
        val attendanceByAdmin = getAttendanceByAdmin(company, employee, markAttendanceRequest.forDate)
        if (attendanceByAdmin != null) {
            // Already present so only update
            logger.debug("AttendanceByAdmin Already present so only update")
            attendanceByAdmin.addedBy = addedByUser
            attendanceByAdmin.workingMinutes = workingMinutes
            attendanceByAdmin.attendanceType = markAttendanceRequest.attendanceType
            return attendanceByAdminRepository.save(attendanceByAdmin)
        }

        // Save new one
        logger.debug("Save new AttendanceByAdmin")
        val newAttendance = AttendanceByAdmin()
        newAttendance.id = getAttendanceByAdminKey(companyId = company.id, employeeId = employee.id, forDate = markAttendanceRequest.forDate)
        newAttendance.attendanceType = markAttendanceRequest.attendanceType
        newAttendance.workingMinutes = workingMinutes
        newAttendance.employee = employee
        newAttendance.company = company
        newAttendance.addedBy = addedByUser
        return attendanceByAdminRepository.save(newAttendance)
    }

    fun getAttendanceSummary(requestedByUser: User, company: Company, attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse? {


        // Choosing a random date in middle to select correct start and end month
        val startDate = LocalDateTime.of(attendanceSummaryRequest.forYear, attendanceSummaryRequest.forMonth, 20, 0, 0, 0, 0)
        val startTime = startDate.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay().minusMonths(2) // get data from past 2 months
        val endTime = startDate.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX)



        val attendances = attendanceRepository.getAllAttendancesBetweenGivenTimes(
            companyId = company.id,
            startTime = startTime,
            endTime = endTime
        )

        val attendancesByAdmin = attendanceByAdminRepository.getAllAttendancesByAdminBetweenGivenTimes(
            companyId = company.id,
            startTime = startTime,
            endTime = endTime
        )

        val holidays = holidayRepository.getAllHolidaysBetweenGivenTimes(
            companyId = company.id,
            startTime = startTime,
            endTime = endTime
        )

        val overtimes = overtimeRepository.getAllOvertimesBetweenGivenTimes(
            companyId = company.id,
            startTime = startTime,
            endTime = endTime
        )

        val lateFines = lateFineRepository.getAllLateFineBetweenGivenTimes(
            companyId = company.id,
            startTime = startTime,
            endTime = endTime
        )


        TODO()
    }

}
