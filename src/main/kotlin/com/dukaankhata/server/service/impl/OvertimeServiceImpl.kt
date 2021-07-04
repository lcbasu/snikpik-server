package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveOvertimeRequest
import com.dukaankhata.server.dto.SavedOvertimeResponse
import com.dukaankhata.server.service.OvertimeService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.OvertimeProvider
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
            companyId = saveOvertimeRequest.companyId,
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
