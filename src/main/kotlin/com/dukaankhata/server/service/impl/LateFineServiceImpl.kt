package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveLateFineRequest
import com.dukaankhata.server.dto.SavedLateFineResponse
import com.dukaankhata.server.service.LateFineService
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.LateFineUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LateFineServiceImpl : LateFineService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var lateFineUtils: LateFineUtils

    override fun saveLateFine(saveLateFineRequest: SaveLateFineRequest): SavedLateFineResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = saveLateFineRequest.employeeId,
            companyId = saveLateFineRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        return lateFineUtils.saveLateFine(
            addedBy = requestContext.user,
            company = requestContext.company!!,
            employee = requestContext.employee!!,
            forDate = saveLateFineRequest.forDate,
            saveLateFineRequest = saveLateFineRequest
        )
    }

}
