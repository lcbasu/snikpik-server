package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.AttendanceRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.Attendance
import com.dukaankhata.server.service.AttendanceService
import com.dukaankhata.server.service.converter.AttendanceServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CompanyUtils
import com.dukaankhata.server.utils.EmployeeUtils
import com.dukaankhata.server.utils.UserRoleUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class AttendanceServiceImpl : AttendanceService() {

    @Autowired
    private lateinit var attendanceRepository: AttendanceRepository

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var attendanceServiceConverter: AttendanceServiceConverter

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    override fun saveAttendance(saveAttendanceRequest: SaveAttendanceRequest): SavedAttendanceResponse? {
        val punchByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(saveAttendanceRequest.companyId)
        val employee = employeeUtils.getEmployee(saveAttendanceRequest.employeeId)
        if (punchByUser == null || company == null || employee == null) {
            error("User, Company, and Employee are required to add an employee");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = punchByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can mark the attendance");
        }

        // TODO: Employee 1 can not mark attendance for Employee 2 unless

        val attendance = attendanceRepository.let {
            val newAttendance = Attendance()

            newAttendance.forDate = saveAttendanceRequest.forDate

            newAttendance.punchBy = punchByUser
            newAttendance.punchAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(saveAttendanceRequest.punchAt/1000), ZoneId.of("UTC"))
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
        return attendanceServiceConverter.getSavedAttendanceResponse(attendance)
    }

    override fun getAttendances(getAttendancesRequest: GetAttendancesRequest): AttendancesResponse? {
        val punchByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(getAttendancesRequest.companyId)
        if (punchByUser == null || company == null) {
            error("User, Company, and Employee are required to add an employee");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = punchByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can get the attendances");
        }

        val attendances = attendanceRepository.getAttendanceByCompanyAndDates(
            company = company,
            forDates = getAttendancesRequest.forDates
        )

        return attendanceServiceConverter.getAttendancesResponse(company, attendances)
    }

    override fun getAttendanceInfo(attendanceInfoRequest: AttendanceInfoRequest): AttendanceInfoResponse? {
        val requestingUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(attendanceInfoRequest.companyId)
        if (requestingUser == null || company == null) {
            error("User, and Company are required to get attendance");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(user = requestingUser, company = company) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can get the attendances");
        }

        val attendances = attendanceRepository.findByCompanyAndForDate(
            company = company, forDate = attendanceInfoRequest.forDate
        )

        // get all employees who were on payroll that day
        val employeesForDate = employeeUtils.getEmployeesForDate(company.id, attendanceInfoRequest.forDate)

        return attendanceServiceConverter.getAttendanceInfo(company, employeesForDate, attendances, attendanceInfoRequest.forDate)
    }

}
