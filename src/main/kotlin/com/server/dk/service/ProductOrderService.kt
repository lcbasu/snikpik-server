package com.server.dk.service

import com.server.dk.dto.*

abstract class ProductOrderService {
    abstract fun productOrderUpdateStatusUpdate(productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): SavedProductOrderResponse
    abstract fun productOrderUpdate(productOrderUpdateRequest: ProductOrderUpdateRequest): SavedProductOrderResponse
    abstract fun placeProductOrder(placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse
    abstract fun getAllProductOrders(companyServerIdOrUsername: String): AllProductOrdersResponse
    abstract fun getAllProductOrderCards(companyId: String): AllProductOrderCardsResponse
    abstract fun getProductOrder(orderId: String): SavedProductOrderResponse
    abstract fun getProductOrderStateChanges(orderId: String): AllProductOrderStateChangesResponse
    abstract fun getAllDeliveryTimeIds(): AllDeliveryTimeIdsResponse
}
