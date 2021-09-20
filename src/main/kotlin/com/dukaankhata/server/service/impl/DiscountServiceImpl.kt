package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveDiscountRequest
import com.dukaankhata.server.dto.SavedDiscountResponse
import com.dukaankhata.server.dto.toSavedDiscountResponse
import com.dukaankhata.server.service.DiscountService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.DiscountProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DiscountServiceImpl : DiscountService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var discountProvider: DiscountProvider

    override fun saveDiscount(saveDiscountRequest: SaveDiscountRequest): SavedDiscountResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = saveDiscountRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val discount = discountProvider.saveDiscount(
            addedBy = requestContext.user,
            company = company,
            saveDiscountRequest = saveDiscountRequest
        )
        return discount.toSavedDiscountResponse()
    }
}
