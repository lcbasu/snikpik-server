package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.CartItem
import com.dukaankhata.server.enums.CartItemUpdateAction
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCartRequest(
    val productId: String,
    val action: CartItemUpdateAction
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCartItemResponse(
    val serverId: String,
    val totalUnits: Long = 0,
    val taxPerUnitInPaisa: Long = 0,
    val pricePerUnitInPaisa: Long = 0,
    val totalTaxInPaisa: Long = 0,
    val totalPriceWithoutTaxInPaisa: Long = 0,
    val orderId: String?,
    val product: SavedProductResponse?
)


fun CartItem.toSavedCartItemResponse(): SavedCartItemResponse {
    this.apply {
        return SavedCartItemResponse(
            serverId = id.toString(),
            totalUnits = totalUnits,
            taxPerUnitInPaisa = taxPerUnitInPaisa,
            pricePerUnitInPaisa = pricePerUnitInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            orderId = productOrder?.id,
            product = product?.toSavedProductResponse()
        )
    }
}
