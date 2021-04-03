package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveOvertimeRequest
import com.dukaankhata.server.dto.SavedOvertimeResponse
import com.dukaankhata.server.service.OvertimeService
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.OvertimeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OvertimeServiceImpl : OvertimeService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var overtimeUtils: OvertimeUtils

    override fun saveOvertime(saveOvertimeRequest: SaveOvertimeRequest): SavedOvertimeResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = saveOvertimeRequest.employeeId,
            companyId = saveOvertimeRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        return overtimeUtils.saveOvertime(
            addedBy = requestContext.user,
            company = requestContext.company!!,
            employee = requestContext.employee!!,
            forDate = saveOvertimeRequest.forDate,
            saveOvertimeRequest = saveOvertimeRequest
        )
    }

}
