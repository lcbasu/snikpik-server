package com.server.dk.service.impl

import com.server.dk.dto.SaveDiscountRequest
import com.server.dk.dto.SavedDiscountResponse
import com.server.dk.dto.toSavedDiscountResponse
import com.server.dk.service.DiscountService
import com.server.common.provider.AuthProvider
import com.server.dk.provider.DiscountProvider
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
