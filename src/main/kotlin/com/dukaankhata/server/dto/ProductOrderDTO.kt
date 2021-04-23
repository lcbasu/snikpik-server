package com.dukaankhata.server.dto

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
    var productOrderStatus: ProductOrderStatus,
    var cartItems: List<SavedCartItemResponse>
)

fun ProductOrder.toSavedProductResponse(): SavedProductOrderResponse {
    this.apply {
        return SavedProductOrderResponse(
            serverId = id,
            discountInPaisa = discountInPaisa,
            deliveryChargeInPaisa = deliveryChargeInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            productOrderStatus = productOrderStatus,
            cartItems = emptyList()
        )
    }
}
