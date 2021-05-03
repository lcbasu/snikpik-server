package com.dukaankhata.server.service

import com.dukaankhata.server.dto.PlaceProductOrderRequest
import com.dukaankhata.server.dto.ProductOrderStatusUpdateRequest
import com.dukaankhata.server.dto.ProductOrderUpdateRequest
import com.dukaankhata.server.dto.SavedProductOrderResponse

abstract class ProductOrderService {
    abstract fun productOrderUpdateStatusUpdate(productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): SavedProductOrderResponse
    abstract fun productOrderUpdate(productOrderUpdateRequest: ProductOrderUpdateRequest): SavedProductOrderResponse
    abstract fun placeProductOrder(placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse
}
