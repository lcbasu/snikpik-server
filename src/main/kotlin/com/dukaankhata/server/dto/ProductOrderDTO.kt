package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.enums.ProductOrderStatus
import com.dukaankhata.server.enums.ProductOrderUpdateType
import com.dukaankhata.server.enums.ProductOrderUpdatedBy
import com.dukaankhata.server.model.ProductOrderStateBeforeUpdate
import com.dukaankhata.server.model.getProductOrderStateBeforeUpdate
import com.dukaankhata.server.utils.CartItemUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class ProductOrderStatusUpdateRequest(
    val productOrderId: String,
    val updatedBy: ProductOrderUpdatedBy,
    val updateType: ProductOrderUpdateType
)

//@JsonTypeInfo(
//    use = JsonTypeInfo.Id.NAME,
//    include = JsonTypeInfo.As.PROPERTY,
//    property = "type"
//)
//@JsonSubTypes(
//    JsonSubTypes.Type(value = ProductOrderUpdateByCustomerRequest::class, name = "ProductOrderUpdateByCustomerRequest"),
//    JsonSubTypes.Type(value = ProductOrderUpdateBySellerRequest::class, name = "ProductOrderUpdateBySellerRequest")
//)
@JsonIgnoreProperties(ignoreUnknown = true)
open class ProductOrderUpdateRequest(
    val updatedBy: ProductOrderUpdatedBy,
    val productOrderId: String? = null,
    // Cart ID to -> New Count
    // Can be updated by both Seller and Customer
    val newCartUpdates: Map<String, Long> = emptyMap()
)

//@JsonTypeName("ProductOrderUpdateByCustomerRequest")
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductOrderUpdateByCustomerRequest(
    val newAddressId: String? = null,
): ProductOrderUpdateRequest(ProductOrderUpdatedBy.BY_CUSTOMER)

//@JsonTypeName("ProductOrderUpdateBySellerRequest")
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductOrderUpdateBySellerRequest(
    val newDeliveryChargeInPaisa: Long? = null,
): ProductOrderUpdateRequest(ProductOrderUpdatedBy.BY_SELLER)

data class ProductOrderStateBeforeUpdateResponse(
    val addressId: String,

    val cartItems: Map<String, Long>,
    val deliveryChargeInPaisa: Long,

    val totalTaxInPaisa: Long,
    val totalPriceWithoutTaxInPaisa: Long,
    val totalPricePayableInPaisa: Long,
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
    var orderStatus: ProductOrderStatus = ProductOrderStatus.DRAFT,
    var cartItems: List<SavedCartItemResponse> = emptyList(),
    var discount: SavedDiscountResponse? = null,
    var address: SavedAddressResponse? = null,
    var productOrderStateBeforeUpdateResponse: ProductOrderStateBeforeUpdateResponse? = null,
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
            productOrderStateBeforeUpdateResponse = productOrderStateBeforeUpdate?.let { getProductOrderStateBeforeUpdate()?.toProductOrderUpdateResponse() }
        )
    }
}

fun ProductOrderStateBeforeUpdate.toProductOrderUpdateResponse(): ProductOrderStateBeforeUpdateResponse {
    this.apply {
        return ProductOrderStateBeforeUpdateResponse(
            addressId = addressId,
            cartItems = cartItems,
            deliveryChargeInPaisa = deliveryChargeInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = totalPricePayableInPaisa,
        )
    }
}
