package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.enums.ProductOrderStatus
import com.dukaankhata.server.enums.ProductOrderUpdateType
import com.dukaankhata.server.model.ProductOrderUpdate
import com.dukaankhata.server.model.getProductOrderUpdate
import com.dukaankhata.server.utils.CartItemUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class ProductOrderUpdateApprovalRequest(
    val productOrderId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
open class ProductOrderUpdateRequest(
    val type: ProductOrderUpdateType,
    val productOrderId: String? = null,
    // Cart ID to -> New Count
    // Can be updated by both Seller and Customer
    val newCartUpdates: Map<String, Long> = emptyMap()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductOrderUpdateByCustomerRequest(
    val newAddressId: String? = null,
): ProductOrderUpdateRequest(ProductOrderUpdateType.BY_CUSTOMER)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductOrderUpdateBySellerRequest(
    val newDeliveryChargeInPaisa: Long? = null,
): ProductOrderUpdateRequest(ProductOrderUpdateType.BY_SELLER)

data class ProductOrderUpdateResponse(
    val newTotalTaxInPaisa: Long?, // -> INDIRECTLY UPDATED
    val newTotalPriceWithoutTaxInPaisa: Long?, // -> INDIRECTLY UPDATED
    val newTotalPricePayableInPaisa: Long?, // -> INDIRECTLY UPDATED

    val newDeliveryChargeInPaisa: Long?, // -> DIRECTLY UPDATED
    val newAddressId: String?, // -> DIRECTLY UPDATED
    // Cart ID to -> New Count
    val newCartUpdates: Map<String, Long> = emptyMap() // -> DIRECTLY UPDATED
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlaceProductOrderRequest(
    val productOrderId: String
)

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
    var address: SavedAddressResponse? = null,
    var productOrderUpdateResponse: ProductOrderUpdateResponse? = null,
//    var productOrderUpdateBySellerResponse: ProductOrderUpdateResponse? = null,
//    var productOrderUpdateByCustomerResponse: ProductOrderUpdateResponse? = null
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
            cartItems = cartItemUtils.getCartItems(this).filterNot { it.totalUnits == 0L }.map { it.toSavedCartItemResponse() },
            address = address?.let { it.toSavedAddressResponse() },
            discount = discount?.let { it.toSavedDiscountResponse() },
            productOrderUpdateResponse = productOrderUpdate?.let { getProductOrderUpdate().toProductOrderUpdateResponse() }
        )
    }
}

fun ProductOrderUpdate.toProductOrderUpdateResponse(): ProductOrderUpdateResponse {
    this.apply {
        return ProductOrderUpdateResponse(
            newTotalTaxInPaisa = newTotalTaxInPaisa,
            newTotalPriceWithoutTaxInPaisa = newTotalPriceWithoutTaxInPaisa,
            newTotalPricePayableInPaisa = newTotalPricePayableInPaisa,
//            newDiscountInPaisa = newDiscountInPaisa,
            newDeliveryChargeInPaisa = newDeliveryChargeInPaisa,
            newAddressId = newAddressId,
            newCartUpdates = newCartUpdates,
        )
    }
}
