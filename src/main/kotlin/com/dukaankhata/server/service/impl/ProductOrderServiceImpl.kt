package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.ProductOrderService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.CartItemProvider
import com.dukaankhata.server.provider.ProductOrderProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductOrderServiceImpl : ProductOrderService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var productOrderProvider: ProductOrderProvider

    @Autowired
    private lateinit var cartItemProvider: CartItemProvider

    override fun productOrderUpdateStatusUpdate(productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.productOrderUpdateApproval(
            requestContext.user,
            productOrderStatusUpdateRequest)
        return updatedProductOrder.toSavedProductOrderResponse(cartItemProvider)
    }

    override fun productOrderUpdate(productOrderUpdateRequest: ProductOrderUpdateRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.productOrderUpdate(requestContext.user, productOrderUpdateRequest)
        return updatedProductOrder.toSavedProductOrderResponse(cartItemProvider)
    }

    override fun placeProductOrder(placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.placeProductOrder(
            user = requestContext.user,
            productOrderId = placeProductOrderRequest.productOrderId)
        return updatedProductOrder.toSavedProductOrderResponse(cartItemProvider)
    }

    override fun getAllProductOrders(companyId: String): AllProductOrdersResponse {
        val requestContext = authProvider.validateRequest(
            companyId = companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val productOrders = productOrderProvider.getProductOrders(company)
        return AllProductOrdersResponse(
            orders = productOrders.map { it.toSavedProductOrderResponse(cartItemProvider) }
        )
    }

    override fun getAllProductOrderCards(companyId: String): AllProductOrderCardsResponse {
        val requestContext = authProvider.validateRequest(
            companyId = companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val productOrders = productOrderProvider.getProductOrders(company)
        return AllProductOrderCardsResponse(
            orders = productOrders.map { it.toProductOrderCardResponse(cartItemProvider) }
        )
    }
}
