package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.enums.SalaryType
import com.dukaankhata.server.service.EmployeeService
import com.dukaankhata.server.service.PdfService
import com.dukaankhata.server.service.SchedulerService
import com.dukaankhata.server.service.converter.EmployeeServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CompanyUtils
import com.dukaankhata.server.utils.EmployeeUtils
import com.dukaankhata.server.utils.UserRoleUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EmployeeServiceImpl : EmployeeService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var schedulerService: SchedulerService

    @Autowired
    private lateinit var pdfService: PdfService

    override fun saveEmployee(saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse? {
        val createdByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(saveEmployeeRequest.companyId)
        if (createdByUser == null || company == null) {
            error("User and Company are required to add an employee");
        }
        val createdForUser = authUtils.getOrCreateUserByPhoneNumber(saveEmployeeRequest.phoneNumber)
            ?: error("User should be created for new employee without any user account")
        val employee = employeeUtils.saveEmployee(
            createdByUser = createdByUser,
            createdForUser = createdForUser,
            company = company,
            saveEmployeeRequest = saveEmployeeRequest
        )
        // Save the user role for the employee
        userRoleUtils.addUserRole(createdForUser, company, RoleType.EMPLOYEE_NON_ADMIN)
            ?: error("Unable to save user role for the employee")

        if (employee.salaryType != SalaryType.ONE_TIME) {
            schedulerService.scheduleEmployeeSalaryUpdate(employee);
        }

        return employeeServiceConverter.getSavedEmployeeResponse(employee);
    }

    override fun getCompanyEmployees(companyServerId: Long): CompanyEmployeesResponse? {
        val requestingUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(companyServerId)
        if (requestingUser == null || company == null) {
            error("User and Company are required to get list of employees");
        }
        val userRoles = requestingUser.let { userRoleUtils?.getUserRolesForUserAndCompany(user = it, company = company) } ?: emptyList()
        // Return the list of employees, only for employer and admin employees
        val validRoles = userRoles.filter { it.id?.roleType == RoleType.EMPLOYER.name || it.id?.roleType == RoleType.EMPLOYEE_ADMIN.name }
        return if (validRoles.isNotEmpty()) {
            employeeServiceConverter.getCompanyEmployeesResponse(company, employeeUtils.findByCompany(company) ?: emptyList());
        } else {
            null
        }
    }

    override fun removeEmployee(removeEmployeeRequest: RemoveEmployeeRequest): SavedEmployeeResponse? {
        val employee = employeeUtils.removeEmployee(removeEmployeeRequest)
        if (employee.salaryType != SalaryType.ONE_TIME) {
            schedulerService.unScheduleEmployeeSalaryUpdate(employee);
        }
        return employeeServiceConverter.getSavedEmployeeResponse(employee)
    }

    override fun updateSalary(employeeId: Long, forDate: String): SavedEmployeeResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        val employee = requestContext.employee!!

        employeeUtils.updateSalary(employee, forDate)

        return employeeServiceConverter.getSavedEmployeeResponse(employee)
    }

    override fun getSalarySlip(employeeId: Long, startDate: String, endDate: String): SalarySlipResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee!!
        val v = pdfService.generateSamplePdf()
        return SalarySlipResponse(
            employee = employeeServiceConverter.getSavedEmployeeResponse(employee),
            startDate = startDate,
            endDate = endDate,
            salarySlipUrl = "URL"
        )
    }
}
