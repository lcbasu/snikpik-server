package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.enums.SalaryType
import com.dukaankhata.server.service.EmployeeService
import com.dukaankhata.server.service.PdfService
import com.dukaankhata.server.service.schedule.EmployeeSalaryUpdateSchedulerService
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
    private lateinit var userRoleUtils: UserRoleUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var employeeSalaryUpdateSchedulerService: EmployeeSalaryUpdateSchedulerService

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
            employeeSalaryUpdateSchedulerService.scheduleEmployeeSalaryUpdate(employee);
        }

        return employee.toSavedEmployeeResponse();
    }

    override fun getCompanyEmployees(companyServerId: String): CompanyEmployeesResponse? {
        val requestingUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(companyServerId)
        if (requestingUser == null || company == null) {
            error("User and Company are required to get list of employees");
        }
        val userRoles = requestingUser.let { userRoleUtils?.getUserRolesForUserAndCompany(user = it, company = company) } ?: emptyList()
        // Return the list of employees, only for employer and admin employees
        val validRoles = userRoles.filter { it.id?.roleType == RoleType.EMPLOYER.name || it.id?.roleType == RoleType.EMPLOYEE_ADMIN.name }
        return if (validRoles.isNotEmpty()) {
            val employees = employeeUtils.findByCompany(company) ?: emptyList()
            return CompanyEmployeesResponse(
                company = company.toSavedCompanyResponse(),
                employees = employees.map {
                    it.toSavedEmployeeResponse()
                })
        } else {
            null
        }
    }

    override fun removeEmployee(removeEmployeeRequest: RemoveEmployeeRequest): SavedEmployeeResponse? {
        val employee = employeeUtils.removeEmployee(removeEmployeeRequest)
        if (employee.salaryType != SalaryType.ONE_TIME) {
            employeeSalaryUpdateSchedulerService.unScheduleEmployeeSalaryUpdate(employee);
        }
        return employee.toSavedEmployeeResponse()
    }

    override fun updateSalary(employeeId: String, forDate: String): SavedEmployeeResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        val employee = requestContext.employee ?: error("Employee mpt found for $employeeId")

        employeeUtils.updateSalary(employee, forDate)

        return employee.toSavedEmployeeResponse()
    }

    override fun getSalarySlip(employeeId: String, startDate: String, endDate: String): SalarySlipResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee!!
        val v = pdfService.generateSamplePdf()
        return SalarySlipResponse(
            employee = employee.toSavedEmployeeResponse(),
            startDate = startDate,
            endDate = endDate,
            salarySlipUrl = "URL"
        )
    }

    override fun updateEmployeeJoiningDate(updateEmployeeJoiningDateRequest: UpdateEmployeeJoiningDateRequest): SavedEmployeeResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = updateEmployeeJoiningDateRequest.employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee ?: error("Employee mpt found for ${updateEmployeeJoiningDateRequest.employeeId}")
        return employeeUtils.updateEmployeeJoiningDate(employee, updateEmployeeJoiningDateRequest.newJoiningTime).toSavedEmployeeResponse()
    }
}
