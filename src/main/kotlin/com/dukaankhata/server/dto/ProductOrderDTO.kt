package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.entities.ProductOrderStateChange
import com.dukaankhata.server.entities.getMediaDetails
import com.dukaankhata.server.entities.orderUpdatable
import com.dukaankhata.server.enums.*
import com.dukaankhata.server.model.*
import com.dukaankhata.server.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class ProductOrderStatusUpdateRequest(
    val productOrderId: String,
    val updatedBy: ProductOrderUpdatedBy,
    val updateType: ProductOrderUpdateType,
    // In case of accepting the order and sharing the tentative delivery time
    val deliveryTimeId: DeliveryTimeId?
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
    var orderUpdatable: Boolean,
    var createdAt: Long,
    var updatedAt: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedProductOrderStateChangeResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val addedByUser: SavedUserResponse,
    val productOrder: SavedProductOrderResponse,
    var productOrderStatus: ProductOrderStatus = ProductOrderStatus.DRAFT,
    var stateChangeAt: Long = 0,
    var productOrderStateChangeData: ProductOrderStateChangeData? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProductOrderStateChangesResponse(
    val changes: List<SavedProductOrderStateChangeResponse>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MigratedProductOrderResponse(
    val fromUser: SavedUserResponse,
    val toUser: SavedUserResponse,
    val fromProductOrders: List<SavedProductOrderResponse>,
    val toProductOrders: List<SavedProductOrderResponse>,
)

fun ProductOrderStateChange.toSavedProductOrderStateChangeResponse(): SavedProductOrderStateChangeResponse {
    this.apply {
        return SavedProductOrderStateChangeResponse(
            serverId = id,
            company = company!!.toSavedCompanyResponse(),
            addedByUser = addedBy!!.toSavedUserResponse(),
            productOrder = productOrder!!.toSavedProductOrderResponse(),
            productOrderStatus = productOrderStatus,
            stateChangeAt = DateUtils.getEpoch(stateChangeAt),
            productOrderStateChangeData = getProductOrderStateChangeData()
        )
    }
}

fun ProductOrder.toSavedProductOrderResponse(): SavedProductOrderResponse {
    this.apply {
        val ci = cartItems.filterNot { it.totalUnits == 0L }.map { it.toSavedCartItemResponse() }
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
            cartItems = ci,
            address = address?.let { it.toSavedAddressResponse() },
            discount = discount?.let { it.toSavedDiscountResponse() },
            productOrderStateBeforeUpdateResponse = productOrderStateBeforeUpdate?.let { getProductOrderStateBeforeUpdate()?.toProductOrderUpdateResponse() },
            mediaDetails = MediaDetails(ci.mapNotNull { it.product?.mediaDetails?.media }.flatten()),
            paymentMode = paymentMode,
            successPaymentId = successPaymentId ?: "",
            cartItemsCount = ci.sumBy { it.totalUnits.toInt() },
            orderedAt = DateUtils.getEpoch(createdAt),
            orderUpdatable = orderUpdatable(),
            createdAt = DateUtils.getEpoch(createdAt),
            updatedAt = DateUtils.getEpoch(lastModifiedAt),
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliveryTimeIdResponse(
    val id: DeliveryTimeId,
    val rank: Int,
    val displayName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllDeliveryTimeIdsResponse(
    val deliveryTimeIds: List<DeliveryTimeIdResponse>
)

fun ProductOrder.toProductOrderCardResponse(): ProductOrderCardResponse {
    this.apply {
        return ProductOrderCardResponse(
            serverId = id,
            mediaDetails = MediaDetails(cartItems.asSequence().filterNotNull().filterNot { it.totalUnits == 0L }.map { it.product?.getMediaDetails()?.media }.filterNotNull().flatten()
                .toList()),
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            orderStatus = orderStatus,
            cartItemsCount = cartItems.filterNot { it.totalUnits == 0L }.size,
            orderedAt = DateUtils.getEpoch(createdAt),
            paymentMode = OrderPaymentMode.COD
        )
    }
}
