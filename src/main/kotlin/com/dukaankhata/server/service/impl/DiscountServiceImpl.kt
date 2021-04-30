package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveDiscountRequest
import com.dukaankhata.server.dto.SavedDiscountResponse
import com.dukaankhata.server.dto.toSavedDiscountResponse
import com.dukaankhata.server.service.DiscountService
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.DiscountUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DiscountServiceImpl : DiscountService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var discountUtils: DiscountUtils

    override fun saveDiscount(saveDiscountRequest: SaveDiscountRequest): SavedDiscountResponse {
        val requestContext = authUtils.validateRequest(
            companyId = saveDiscountRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val discount = discountUtils.saveDiscount(
            addedBy = requestContext.user,
            company = company,
            saveDiscountRequest = saveDiscountRequest
        )
        return discount.toSavedDiscountResponse()
    }
}
