package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.AttendanceRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.service.AttendanceService
import com.dukaankhata.server.service.converter.AttendanceServiceConverter
import com.dukaankhata.server.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
    private lateinit var attendanceUtils: AttendanceUtils

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
        val attendance = attendanceUtils.saveAttendance(punchByUser, company, employee, saveAttendanceRequest)
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

    override fun markAttendance(markAttendanceRequest: MarkAttendanceRequest): SavedAttendanceByAdminResponse? {
        val requestingUser = authUtils.getRequestUserEntity()
        val employee = employeeUtils.getEmployee(markAttendanceRequest.employeeId)

        if (requestingUser == null || employee == null) {
            error("User, and Employee are required to add an employee");
        }

        val company = employee.company ?: error("Employee should always have company")

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = requestingUser,
            company = company
        ) ?: emptyList()
        val validRoles = userRoles.filter { it.id?.roleType == RoleType.EMPLOYER.name || it.id?.roleType == RoleType.EMPLOYEE_ADMIN.name }
        if (validRoles.isEmpty()) {
            error("Only employer or admin employees of the company can mark the attendance");
        }
        val attendance = attendanceUtils.markAttendance(requestingUser, company, employee, markAttendanceRequest)
        return attendanceServiceConverter.getSavedAttendanceByAdminResponse(attendance)
    }

    override fun getAttendanceSummary(attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse? {
        val requestedByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(attendanceSummaryRequest.companyId)
        if (requestedByUser == null || company == null) {
            error("User, and Company are required to get payment summary");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = requestedByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can view payment");
        }
        return attendanceUtils.getAttendanceSummary(requestedByUser, company, attendanceSummaryRequest)
    }

}
