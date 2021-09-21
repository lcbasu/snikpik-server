package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class ProductOrderService {
    abstract fun productOrderUpdateStatusUpdate(productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): SavedProductOrderResponse
    abstract fun productOrderUpdate(productOrderUpdateRequest: ProductOrderUpdateRequest): SavedProductOrderResponse
    abstract fun placeProductOrder(placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse
    abstract fun getAllProductOrders(companyServerIdOrUsername: String): AllProductOrdersResponse
    abstract fun getAllProductOrderCards(companyId: String): AllProductOrderCardsResponse
    abstract fun getProductOrder(orderId: String): SavedProductOrderResponse
    abstract fun getProductOrderStateChanges(orderId: String): AllProductOrderStateChangesResponse
}
