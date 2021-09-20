package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.provider.*
import com.dukaankhata.server.service.ProductOrderService
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

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    override fun productOrderUpdateStatusUpdate(productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.productOrderUpdateApproval(
            requestContext.user,
            productOrderStatusUpdateRequest)
        return updatedProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider)
    }

    override fun productOrderUpdate(productOrderUpdateRequest: ProductOrderUpdateRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.productOrderUpdate(requestContext.user, productOrderUpdateRequest)
        return updatedProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider)
    }

    override fun placeProductOrder(placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.placeProductOrder(
            user = requestContext.user,
            productOrderId = placeProductOrderRequest.productOrderId)
        return updatedProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider)
    }

    override fun getAllProductOrders(companyServerIdOrUsername: String): AllProductOrdersResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyServerIdOrUsername,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val productOrders = productOrderProvider.getProductOrders(company)
        return AllProductOrdersResponse(
            orders = productOrders.map { it.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider) }
        )
    }

    override fun getAllProductOrderCards(companyId: String): AllProductOrderCardsResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val productOrders = productOrderProvider.getProductOrders(company)
        return AllProductOrderCardsResponse(
            orders = productOrders.map { it.toProductOrderCardResponse(productVariantProvider, cartItemProvider, productCollectionProvider) }
        )
    }

    override fun getProductOrder(orderId: String): SavedProductOrderResponse {
        authProvider.validateRequest()
        val productOrder = productOrderProvider.getProductOrder(orderId) ?: error("No order found for orderId: $orderId")
        return productOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider)
    }
}
