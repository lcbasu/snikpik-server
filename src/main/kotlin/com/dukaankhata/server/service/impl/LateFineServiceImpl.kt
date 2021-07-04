package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveLateFineRequest
import com.dukaankhata.server.dto.SavedLateFineResponse
import com.dukaankhata.server.service.LateFineService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.LateFineProvider
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
            companyId = saveLateFineRequest.companyId,
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
