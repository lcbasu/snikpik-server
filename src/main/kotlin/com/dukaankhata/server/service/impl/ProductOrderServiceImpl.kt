package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.ProductOrderService
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CartItemUtils
import com.dukaankhata.server.utils.ProductOrderUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductOrderServiceImpl : ProductOrderService() {
    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var productOrderUtils: ProductOrderUtils

    @Autowired
    private lateinit var cartItemUtils: CartItemUtils

    override fun productOrderUpdateStatusUpdate(productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): SavedProductOrderResponse {
        val requestContext = authUtils.validateRequest()
        val updatedProductOrder = productOrderUtils.productOrderUpdateApproval(
            requestContext.user,
            productOrderStatusUpdateRequest)
        return updatedProductOrder.toSavedProductOrderResponse(cartItemUtils)
    }

    override fun productOrderUpdate(productOrderUpdateRequest: ProductOrderUpdateRequest): SavedProductOrderResponse {
        val requestContext = authUtils.validateRequest()
        val updatedProductOrder = productOrderUtils.productOrderUpdate(requestContext.user, productOrderUpdateRequest)
        return updatedProductOrder.toSavedProductOrderResponse(cartItemUtils)
    }

    override fun placeProductOrder(placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse {
        val requestContext = authUtils.validateRequest()
        val updatedProductOrder = productOrderUtils.placeProductOrder(
            user = requestContext.user,
            productOrderId = placeProductOrderRequest.productOrderId)
        return updatedProductOrder.toSavedProductOrderResponse(cartItemUtils)
    }
}
