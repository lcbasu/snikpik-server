package com.server.dk.service.impl

import com.server.dk.dto.SaveOvertimeRequest
import com.server.dk.dto.SavedOvertimeResponse
import com.server.dk.service.OvertimeService
import com.server.dk.provider.AuthProvider
import com.server.dk.provider.OvertimeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OvertimeServiceImpl : OvertimeService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var overtimeProvider: OvertimeProvider

    override fun saveOvertime(saveOvertimeRequest: SaveOvertimeRequest): SavedOvertimeResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = saveOvertimeRequest.employeeId,
            companyServerIdOrUsername = saveOvertimeRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )

        return overtimeProvider.saveOvertime(
            addedBy = requestContext.user,
            company = requestContext.company!!,
            employee = requestContext.employee!!,
            forDate = saveOvertimeRequest.forDate,
            saveOvertimeRequest = saveOvertimeRequest
        )
    }

}
