package com.server.dk.service.impl

import com.server.dk.dto.SaveLateFineRequest
import com.server.dk.dto.SavedLateFineResponse
import com.server.dk.service.LateFineService
import com.server.common.provider.AuthProvider
import com.server.dk.provider.LateFineProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LateFineServiceImpl : LateFineService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var lateFineProvider: LateFineProvider

    override fun saveLateFine(saveLateFineRequest: SaveLateFineRequest): SavedLateFineResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = saveLateFineRequest.employeeId,
            companyServerIdOrUsername = saveLateFineRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )

        return lateFineProvider.saveLateFine(
            addedBy = requestContext.user,
            company = requestContext.company!!,
            employee = requestContext.employee!!,
            forDate = saveLateFineRequest.forDate,
            saveLateFineRequest = saveLateFineRequest
        )
    }

}
