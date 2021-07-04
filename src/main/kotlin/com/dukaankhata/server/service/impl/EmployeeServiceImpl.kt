package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.enums.SalaryType
import com.dukaankhata.server.service.EmployeeService
import com.dukaankhata.server.service.schedule.EmployeeSalaryUpdateSchedulerService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.CompanyProvider
import com.dukaankhata.server.provider.EmployeeProvider
import com.dukaankhata.server.provider.UserRoleProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EmployeeServiceImpl : EmployeeService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var userRoleProvider: UserRoleProvider

    @Autowired
    private lateinit var employeeProvider: EmployeeProvider

    @Autowired
    private lateinit var employeeSalaryUpdateSchedulerService: EmployeeSalaryUpdateSchedulerService

    override fun saveEmployee(saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse? {
        val createdByUser = authProvider.getRequestUserEntity()
        val company = companyProvider.getCompany(saveEmployeeRequest.companyId)
        if (createdByUser == null || company == null) {
            error("User and Company are required to add an employee");
        }
        val createdForUser = authProvider.getOrCreateUserByPhoneNumber(saveEmployeeRequest.phoneNumber)
            ?: error("User should be created for new employee without any user account")
        val employee = employeeProvider.saveEmployee(
            createdByUser = createdByUser,
            createdForUser = createdForUser,
            company = company,
            saveEmployeeRequest = saveEmployeeRequest
        )
        // Save the user role for the employee
        userRoleProvider.addUserRole(createdForUser, company, RoleType.EMPLOYEE_NON_ADMIN)
            ?: error("Unable to save user role for the employee")

        if (employee.salaryType != SalaryType.ONE_TIME) {
            employeeSalaryUpdateSchedulerService.scheduleEmployeeSalaryUpdate(employee);
        }

        return employee.toSavedEmployeeResponse();
    }

    override fun getCompanyEmployees(companyServerId: String): CompanyEmployeesResponse? {
        val requestingUser = authProvider.getRequestUserEntity()
        val company = companyProvider.getCompany(companyServerId)
        if (requestingUser == null || company == null) {
            error("User and Company are required to get list of employees");
        }
        val userRoles = requestingUser.let { userRoleProvider?.getUserRolesForUserAndCompany(user = it, company = company) } ?: emptyList()
        // Return the list of employees, only for employer and admin employees
        val validRoles = userRoles.filter { it.id?.roleType == RoleType.EMPLOYER.name || it.id?.roleType == RoleType.EMPLOYEE_ADMIN.name }
        return if (validRoles.isNotEmpty()) {
            val employees = employeeProvider.findByCompany(company) ?: emptyList()
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
        val employee = employeeProvider.removeEmployee(removeEmployeeRequest)
        if (employee.salaryType != SalaryType.ONE_TIME) {
            employeeSalaryUpdateSchedulerService.unScheduleEmployeeSalaryUpdate(employee);
        }
        return employee.toSavedEmployeeResponse()
    }

    override fun updateSalary(employeeId: String, forDate: String): SavedEmployeeResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = employeeId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )

        val employee = requestContext.employee ?: error("Employee mpt found for $employeeId")

        employeeProvider.updateSalary(employee, forDate)

        return employee.toSavedEmployeeResponse()
    }

    override fun getSalarySlip(employeeId: String, startDate: String, endDate: String): SalarySlipResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = employeeId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee!!
        return employeeProvider.generatePdfForSalarySlip(employee, startDate, endDate)
    }

    override fun updateEmployeeJoiningDate(updateEmployeeJoiningDateRequest: UpdateEmployeeJoiningDateRequest): SavedEmployeeResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = updateEmployeeJoiningDateRequest.employeeId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee ?: error("Employee mpt found for ${updateEmployeeJoiningDateRequest.employeeId}")
        return employeeProvider.updateEmployeeJoiningDate(employee, updateEmployeeJoiningDateRequest.newJoiningTime).toSavedEmployeeResponse()
    }
}
