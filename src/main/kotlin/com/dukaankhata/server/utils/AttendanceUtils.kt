package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.AttendanceRepository
import com.dukaankhata.server.dto.SaveAttendanceRequest
import com.dukaankhata.server.entities.Attendance
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AttendanceUtils {

    @Autowired
    private lateinit var attendanceRepository: AttendanceRepository

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

}
