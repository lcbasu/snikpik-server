package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.enums.OrderPaymentMode
import com.dukaankhata.server.enums.ProductOrderStatus
import com.dukaankhata.server.enums.ProductOrderUpdateType
import com.dukaankhata.server.enums.ProductOrderUpdatedBy
import com.dukaankhata.server.model.MediaDetails
import com.dukaankhata.server.model.ProductOrderStateBeforeUpdate
import com.dukaankhata.server.model.getProductOrderStateBeforeUpdate
import com.dukaankhata.server.provider.CartItemProvider
import com.dukaankhata.server.provider.ProductCollectionProvider
import com.dukaankhata.server.provider.ProductVariantProvider
import com.dukaankhata.server.utils.DateUtils
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
    val paymentId: String? = null, // ID in case of online payment, or COD Payment pickup by some other third party vendor
    val paymentMode: OrderPaymentMode? = null,
): ProductOrderUpdateRequest(ProductOrderUpdatedBy.BY_CUSTOMER)

//@JsonTypeName("ProductOrderUpdateBySellerRequest")
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductOrderUpdateBySellerRequest(
    val newDeliveryChargeInPaisa: Long? = null,
): ProductOrderUpdateRequest(ProductOrderUpdatedBy.BY_SELLER)

data class ProductOrderStateBeforeUpdateResponse(
    val addressId: String? = null,

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
data class AllProductOrdersResponse(
    val orders: List<SavedProductOrderResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedProductOrderResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val addedByUser: SavedUserResponse,
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
    val mediaDetails: MediaDetails,
    var cartItemsCount: Int = 0,
    var orderedAt: Long = 0,
    var paymentMode: OrderPaymentMode,
    var successPaymentId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MigratedProductOrderResponse(
    val fromUser: SavedUserResponse,
    val toUser: SavedUserResponse,
    val fromProductOrders: List<SavedProductOrderResponse>,
    val toProductOrders: List<SavedProductOrderResponse>,
)

fun ProductOrder.toSavedProductOrderResponse(productVariantProvider: ProductVariantProvider, cartItemProvider: CartItemProvider, productCollectionProvider: ProductCollectionProvider): SavedProductOrderResponse {
    this.apply {
        val cartItems = cartItemProvider.getCartItems(this).filterNot { it.totalUnits == 0L }.map { it.toSavedCartItemResponse(productVariantProvider, productCollectionProvider) }
        return SavedProductOrderResponse(
            serverId = id,
            company = company!!.toSavedCompanyResponse(),
            addedByUser = addedBy!!.toSavedUserResponse(),
            discountInPaisa = discountInPaisa,
            deliveryChargeInPaisa = deliveryChargeInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            orderStatus = orderStatus,
            cartItems = cartItems,
            address = address?.let { it.toSavedAddressResponse() },
            discount = discount?.let { it.toSavedDiscountResponse() },
            productOrderStateBeforeUpdateResponse = productOrderStateBeforeUpdate?.let { getProductOrderStateBeforeUpdate()?.toProductOrderUpdateResponse() },
            mediaDetails = MediaDetails(cartItems.mapNotNull { it.product?.mediaDetails?.media }.flatten()),
            paymentMode = paymentMode,
            successPaymentId = successPaymentId ?: "",
            cartItemsCount = cartItems.sumBy { it.totalUnits.toInt() },
            orderedAt = DateUtils.getEpoch(createdAt),
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProductOrderCardsResponse(
    val orders: List<ProductOrderCardResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductOrderCardResponse(
    val serverId: String,
    val mediaDetails: MediaDetails,
    val totalPricePayableInPaisa: Long = 0,
    var orderStatus: ProductOrderStatus = ProductOrderStatus.DRAFT,
    var cartItemsCount: Int = 0,
    var orderedAt: Long = 0,
    var paymentMode: OrderPaymentMode = OrderPaymentMode.NONE
)

fun ProductOrder.toProductOrderCardResponse(productVariantProvider: ProductVariantProvider, cartItemProvider: CartItemProvider, productCollectionProvider: ProductCollectionProvider): ProductOrderCardResponse {
    this.apply {
        val cartItems = cartItemProvider.getCartItems(this).filterNot { it.totalUnits == 0L }.map { it.toSavedCartItemResponse(productVariantProvider, productCollectionProvider) }
        return ProductOrderCardResponse(
            serverId = id,
            mediaDetails = MediaDetails(cartItems.mapNotNull { it.product?.mediaDetails?.media }.flatten()),
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            orderStatus = orderStatus,
            cartItemsCount = cartItems.size,
            orderedAt = DateUtils.getEpoch(createdAt),
            paymentMode = OrderPaymentMode.COD
        )
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductOrderDetailsResponse(
    val serverId: String,
    val mediaDetails: MediaDetails,
    val totalPricePayableInPaisa: Long = 0,
    var orderStatus: ProductOrderStatus,
    var cartItemsCount: Int = 0,
    var orderedAt: Long = 0,
    var paymentMode: OrderPaymentMode,
    val addedByUser: SavedUserResponse,
    var discountInPaisa: Long = 0,
    var deliveryChargeInPaisa: Long = 0,
    val totalTaxInPaisa: Long = 0,
    val totalPriceWithoutTaxInPaisa: Long = 0,
    var cartItems: List<SavedCartItemResponse> = emptyList(),
    var discount: SavedDiscountResponse? = null,
    var address: SavedAddressResponse? = null,
)

fun ProductOrder.toProductOrderDetailsResponse(productVariantProvider: ProductVariantProvider, cartItemProvider: CartItemProvider, productCollectionProvider: ProductCollectionProvider): ProductOrderDetailsResponse {
    this.apply {
        val cartItems = cartItemProvider.getCartItems(this).filterNot { it.totalUnits == 0L }.map { it.toSavedCartItemResponse(productVariantProvider, productCollectionProvider) }
        return ProductOrderDetailsResponse(
            serverId = id,
            addedByUser = addedBy!!.toSavedUserResponse(),
            mediaDetails = MediaDetails(cartItems.mapNotNull { it.product?.mediaDetails?.media }.flatten()),
            discountInPaisa = discountInPaisa,
            deliveryChargeInPaisa = deliveryChargeInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            orderStatus = orderStatus,
            cartItems = cartItems,
            address = address?.let { it.toSavedAddressResponse() },
            discount = discount?.let { it.toSavedDiscountResponse() },
            paymentMode = OrderPaymentMode.COD
        )
    }
}
