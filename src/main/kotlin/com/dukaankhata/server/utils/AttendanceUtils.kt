package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.AttendanceByAdminRepository
import com.dukaankhata.server.dao.AttendanceRepository
import com.dukaankhata.server.dto.MarkAttendanceRequest
import com.dukaankhata.server.dto.SaveAttendanceRequest
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.AttendanceType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AttendanceUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var attendanceRepository: AttendanceRepository

    @Autowired
    private lateinit var attendanceByAdminRepository: AttendanceByAdminRepository

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
            AttendanceType.HOLIDAY_NON_PAID -> 0
            AttendanceType.PRESENT -> company.workingMinutes
            AttendanceType.ABSENT -> 0
            AttendanceType.HALF_DAY -> company.workingMinutes / 2
            AttendanceType.HOLIDAY_PAID -> 0
            AttendanceType.OVERTIME -> 0
            AttendanceType.IN_NOT_MARKED -> 0
            AttendanceType.OUT_NOT_MARKED -> 0
            AttendanceType.NONE -> 0
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

}
