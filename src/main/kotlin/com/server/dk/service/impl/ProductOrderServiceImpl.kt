package com.server.dk.service.impl

import com.server.dk.dto.*
import com.server.dk.enums.DeliveryTimeId
import com.server.dk.enums.toDeliveryTimeIdResponse
import com.server.common.provider.AuthProvider
import com.server.dk.provider.ProductOrderProvider
import com.server.dk.provider.ProductOrderStateChangeProvider
import com.server.dk.service.ProductOrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductOrderServiceImpl : ProductOrderService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var productOrderProvider: ProductOrderProvider

    @Autowired
    private lateinit var productOrderStateChangeProvider: ProductOrderStateChangeProvider

    override fun productOrderUpdateStatusUpdate(productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.productOrderUpdateApproval(
            requestContext.user,
            productOrderStatusUpdateRequest)
        return updatedProductOrder.toSavedProductOrderResponse()
    }

    override fun productOrderUpdate(productOrderUpdateRequest: ProductOrderUpdateRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.productOrderUpdate(requestContext.user, productOrderUpdateRequest)
        return updatedProductOrder.toSavedProductOrderResponse()
    }

    override fun placeProductOrder(placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val updatedProductOrder = productOrderProvider.placeProductOrder(
            user = requestContext.user,
            productOrderId = placeProductOrderRequest.productOrderId)
        return updatedProductOrder.toSavedProductOrderResponse()
    }

    override fun getAllProductOrders(companyServerIdOrUsername: String): AllProductOrdersResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyServerIdOrUsername
        )
        val company = requestContext.company ?: error("Company is required")
        val productOrders = productOrderProvider.getProductOrders(company)
        return AllProductOrdersResponse(
            orders = productOrders
                .filterNot { it.cartItems == null || it.cartItems.isEmpty() }
                .map { it.toSavedProductOrderResponse() }
        )
    }

    override fun getAllProductOrderCards(companyId: String): AllProductOrderCardsResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyId
        )
        val company = requestContext.company ?: error("Company is required")
        val productOrders = productOrderProvider.getProductOrders(company)
        return AllProductOrderCardsResponse(
            orders = productOrders.map { it.toProductOrderCardResponse() }
        )
    }

    override fun getProductOrder(orderId: String): SavedProductOrderResponse {
        authProvider.validateRequest()
        val productOrder = productOrderProvider.getProductOrder(orderId) ?: error("No order found for orderId: $orderId")
        return productOrder.toSavedProductOrderResponse()
    }

    override fun getProductOrderStateChanges(orderId: String): AllProductOrderStateChangesResponse {
        authProvider.validateRequest()
        val productOrder = productOrderProvider.getProductOrder(orderId) ?: error("No order found for orderId: $orderId")
        val stateChanges = productOrderStateChangeProvider.getProductOrderStateChanges(productOrder)
        return AllProductOrderStateChangesResponse(
            changes = stateChanges.map { it.toSavedProductOrderStateChangeResponse() }
        )
    }

    override fun getAllDeliveryTimeIds(): AllDeliveryTimeIdsResponse {
        return AllDeliveryTimeIdsResponse(
            deliveryTimeIds = DeliveryTimeId.values().map {
                it.toDeliveryTimeIdResponse()
            }
        )
    }
}
