package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.CartItem
import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.enums.ProductOrderStatus
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedProductOrderResponse(
    val serverId: String,
    var discountInPaisa: Long = 0,
    var deliveryChargeInPaisa: Long = 0,
    val totalTaxInPaisa: Long = 0,
    val totalPriceWithoutTaxInPaisa: Long = 0,
    val totalPricePayableInPaisa: Long = 0,
//    var deliveryAddress: SavedAddressResponse,
    var orderStatus: ProductOrderStatus = ProductOrderStatus.DRAFT,
    var cartItems: List<SavedCartItemResponse> = emptyList()
)

fun ProductOrder.toSavedProductOrderResponse(): SavedProductOrderResponse {
    this.apply {
        return SavedProductOrderResponse(
            serverId = id,
            discountInPaisa = discountInPaisa,
            deliveryChargeInPaisa = deliveryChargeInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            orderStatus = orderStatus,
        )
    }
}

fun ProductOrder.toSavedProductOrderResponse(cartItems: List<CartItem>): SavedProductOrderResponse {
    this.apply {
        return SavedProductOrderResponse(
            serverId = id,
            discountInPaisa = discountInPaisa,
            deliveryChargeInPaisa = deliveryChargeInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            orderStatus = orderStatus,
            cartItems = cartItems.filterNot { it.totalUnits == 0L }.map { it.toSavedCartItemResponse() }
        )
    }
}
