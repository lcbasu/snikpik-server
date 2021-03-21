package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveOvertimeRequest
import com.dukaankhata.server.dto.SavedOvertimeResponse
import com.dukaankhata.server.service.OvertimeService
import com.dukaankhata.server.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OvertimeServiceImpl : OvertimeService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var overtimeUtils: OvertimeUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    override fun saveOvertime(saveOvertimeRequest: SaveOvertimeRequest): SavedOvertimeResponse? {
        val addedByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(saveOvertimeRequest.companyId)
        val employee = employeeUtils.getEmployee(saveOvertimeRequest.employeeId)
        if (addedByUser == null || company == null || employee == null) {
            error("User, Company, and Employee are required to add an employee");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = addedByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can mark the overtime");
        }

        // TODO: Employee 1 can not mark overtime for Employee 2

        return overtimeUtils.saveOvertime(
            addedBy = addedByUser,
            company = company,
            employee = employee,
            forDate = saveOvertimeRequest.forDate,
            saveOvertimeRequest = saveOvertimeRequest
        )
    }

}
