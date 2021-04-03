package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveLateFineRequest
import com.dukaankhata.server.dto.SavedLateFineResponse
import com.dukaankhata.server.service.LateFineService
import com.dukaankhata.server.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LateFineServiceImpl : LateFineService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var lateFineUtils: LateFineUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    override fun saveLateFine(saveLateFineRequest: SaveLateFineRequest): SavedLateFineResponse? {
        val addedByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(saveLateFineRequest.companyId)
        val employee = employeeUtils.getEmployee(saveLateFineRequest.employeeId)
        if (addedByUser == null || company == null || employee == null) {
            error("User, Company, and Employee are required to add an employee");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = addedByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can mark the lateFine");
        }

        // TODO: Employee 1 can not mark lateFine for Employee 2

        return lateFineUtils.saveLateFine(
            addedBy = addedByUser,
            company = company,
            employee = employee,
            forDate = saveLateFineRequest.forDate,
            saveLateFineRequest = saveLateFineRequest
        )
    }

}
