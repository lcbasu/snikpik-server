package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.enums.ProductOrderStatus
import com.dukaankhata.server.utils.CartItemUtils
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
    var cartItems: List<SavedCartItemResponse> = emptyList(),
    var discount: SavedDiscountResponse? = null,
    var address: SavedAddressResponse? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MigratedProductOrderResponse(
    val fromUser: SavedUserResponse,
    val toUser: SavedUserResponse,
    val fromProductOrders: List<SavedProductOrderResponse>,
    val toProductOrders: List<SavedProductOrderResponse>,
)

fun ProductOrder.toSavedProductOrderResponse(cartItemUtils: CartItemUtils): SavedProductOrderResponse {
    this.apply {
        return SavedProductOrderResponse(
            serverId = id,
            discountInPaisa = discountInPaisa,
            deliveryChargeInPaisa = deliveryChargeInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            orderStatus = orderStatus,
            cartItems = cartItemUtils.getCartItems(this).filterNot { it.totalUnits == 0L }.map { it.toSavedCartItemResponse() }
        )
    }
}
