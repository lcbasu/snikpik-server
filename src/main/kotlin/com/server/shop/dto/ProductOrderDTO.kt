package com.server.shop.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.dto.UserV2PublicMiniDataResponse
import com.server.common.utils.DateUtils
import com.server.shop.entities.CartItemV3
import com.server.shop.entities.ProductOrderPaymentV3
import com.server.shop.entities.ProductOrderV3
import com.server.shop.entities.toUserV2PublicMiniDataResponse
import com.server.shop.enums.*

data class OrderTrackingResponse (
    val orderedAt: Long? = 0,
    val sellerProcessedAt: Long? = 0,
    val shippedAt: Long? = 0,
    val outForDeliveryAt: Long? = 0,
    val deliveredAt: Long? = 0,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProductOrderV3Response (
    val orders: List<SavedProductOrderV3Response>,
)


@JsonIgnoreProperties(ignoreUnknown = true)
open class UpdateDeliveryAddressRequest(
    val productOrderId: String,

    // Either the address id is provided or the address request but not both
    val savedAddressId: String? = null,
    val addressRequest: SaveAddressV3Request? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveForLaterRequest(
    val productOrderId: String,
    val cartItemId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCartV3Request(
    val productVariantId: String,

    val type: ProductOrderType = ProductOrderType.REGULAR_ORDER,

    // either action or newQuantity has to be present
    val action: CartItemUpdateActionV3? = null,
    val newQuantity: Long? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
open class ProductOrderStatusUpdateV3Request(
    val productOrderId: String,
    val updatedBy: ProductOrderUpdatedByV3,
    val updateType: ProductOrderUpdateTypeV3,
    // In case of accepting the order and sharing the tentative delivery time
    val deliveryTimeId: DeliveryTimeIdV3?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliveryTimeIdV3Response(
    val id: DeliveryTimeIdV3,
    val rank: Int,
    val displayName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CreatePaymentOrderRequest (
    val productOrderId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VerifyAndCommitPaymentRequest (
    val productOrderId: String,
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignature: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VerifyAndCommitPaymentResponse (
    val productOrder: SavedProductOrderV3Response,
    val verified: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CreatePaymentOrderResponse (
    val productOrder: SavedProductOrderV3Response,
    val razorpayOrderResponse: RazorpayOrderResponse,
)

data class SavedProductOrderV3Response (

    val id: String = "",

    val type: ProductOrderType = ProductOrderType.REGULAR_ORDER,

    val totalPricePayableInPaisa: Long = 0,
    val totalDiscountInPaisa: Long = 0,
    val totalTaxInPaisa: Long = 0,

    val totalCartItems: Long = 0,
    val totalUnitsInAllCarts: Long = 0,

    val deliveryChargeInPaisa: Long = 0,

    val totalMrpInPaisa: Long,
    val totalSellingPriceInPaisa: Long,

    val priceOfCartItemsWithoutTaxInPaisa: Long = 0,

    val orderStatus: ProductOrderStatusV3 = ProductOrderStatusV3.DRAFT,

    val paymentMode: OrderPaymentModeV3 = OrderPaymentModeV3.NONE,

    val successPayment: ProductOrderPaymentV3Response? = null,

    val deliveryTimeId: DeliveryTimeIdV3Response,

    val minOfMaxDeliveryDateTime: Long?,
    val maxOfMaxDeliveryDateTime: Long?,
    val minOfPromisedDeliveryDateTime: Long?,
    val maxOfPromisedDeliveryDateTime: Long?,
    val firstCartItemDeliveredOnDateTime: Long?,
    val lastCartItemDeliveredOnDateTime: Long?,

    val deliveryAddress: SavedAddressV3Response? = null,

    val cartItems: List<SavedCartItemV3Response>,

//    val appliedCoupon: CouponV3? = null,

    val addedBy: UserV2PublicMiniDataResponse,

    )

data class ProductOrderPaymentV3Response (
    val id: String,
    val paymentMode: OrderPaymentModeV3,
    val paymentStatus: OrderPaymentStatusV3,
    val paymentConfig: Any? = null,
)

data class SavedCartItemV3Response (
    val id: String,
    val productOrderId: String,
    val totalUnits: Long,

    // These 4 fields can change in future so save the values when the order is placed.
    // Start with null. And when the value is null we use the value of the product valiant
    val taxPerUnitInPaisaPaid: Long,
    val pricePerUnitInPaisaPaid: Long,
    val totalTaxInPaisaPaid: Long,
    val totalPriceWithoutTaxInPaisaPaid: Long,

    val totalMrpInPaisa: Long,
    val totalSellingPriceInPaisa: Long,

    val maxDeliveryDateTime: Long?,
    val promisedDeliveryDateTime: Long?,
    val deliveredOnDateTime: Long?,

    // If added to cart from a post
    val postId: String? = null,

    val product: SavedProductV3Response,
    val productVariant: SavedProductVariantV3Response,
)


fun ProductOrderV3.toSavedProductOrderV3Response(): SavedProductOrderV3Response {
    this.apply {
        return SavedProductOrderV3Response(
            id = id,
            type = type,
            totalPricePayableInPaisa = totalPricePayableInPaisa,
            totalDiscountInPaisa = totalDiscountInPaisa,
            totalCartItems = totalCartItems,
            totalUnitsInAllCarts = totalUnitsInAllCarts,
            totalTaxInPaisa = totalTaxInPaisa,
            deliveryChargeInPaisa = deliveryChargeInPaisa,
            priceOfCartItemsWithoutTaxInPaisa = priceOfCartItemsWithoutTaxInPaisa,
            totalMrpInPaisa = totalMrpInPaisa,
            totalSellingPriceInPaisa = totalSellingPriceInPaisa,
            orderStatus = orderStatus,
            paymentMode = paymentMode,
            successPayment = successPayment?.toProductOrderPaymentV3Response(),
            deliveryTimeId = deliveryTimeId.toDeliveryTimeIdV3Response(),
            deliveryAddress = deliveryAddress?.toSavedAddressV3Response(),
            minOfMaxDeliveryDateTime = DateUtils.getEpoch(minOfMaxDeliveryDateTime),
            maxOfMaxDeliveryDateTime = DateUtils.getEpoch(maxOfMaxDeliveryDateTime),
            minOfPromisedDeliveryDateTime = DateUtils.getEpoch(minOfPromisedDeliveryDateTime),
            maxOfPromisedDeliveryDateTime = DateUtils.getEpoch(maxOfPromisedDeliveryDateTime),
            firstCartItemDeliveredOnDateTime = DateUtils.getEpoch(firstCartItemDeliveredOnDateTime),
            lastCartItemDeliveredOnDateTime = DateUtils.getEpoch(lastCartItemDeliveredOnDateTime),
            // Sorting by Id so that on the cart screen, while updating the amount, the ordering remains constant
            // no matter if the middle item units are more than the one at the bottom and top
            cartItems = cartItems.filter { it.totalUnits > 0 }.sortedBy { it.id }.map { it.toSavedCartItemV3Response() },
            addedBy = addedBy!!.toUserV2PublicMiniDataResponse(),
//            appliedCoupon = appliedCoupon,
//            cartItems = cartItems,
        )
    }
}

fun ProductOrderPaymentV3.toProductOrderPaymentV3Response(): ProductOrderPaymentV3Response {
    this.apply {
        return ProductOrderPaymentV3Response(
            id = id,
            paymentMode = paymentMode,
            paymentStatus = paymentStatus,
            paymentConfig = paymentConfig
        )
    }
}

fun CartItemV3.toSavedCartItemV3Response(): SavedCartItemV3Response {
    this.apply {
        return SavedCartItemV3Response(
            id = id,
            productOrderId = productOrder!!.id,
            totalUnits = totalUnits,

            // These 4 fields can change in future so save the values when the order is placed.
            // Start with null. And when the value is null we use the value of the product valiant
            taxPerUnitInPaisaPaid = taxPerUnitInPaisaPaid ?: 0,
            pricePerUnitInPaisaPaid = pricePerUnitInPaisaPaid ?: 0,
            totalTaxInPaisaPaid = totalTaxInPaisaPaid ?: 0,
            totalPriceWithoutTaxInPaisaPaid = totalPriceWithoutTaxInPaisaPaid ?: 0,

            totalMrpInPaisa = totalMrpInPaisa ?: 0,
            totalSellingPriceInPaisa = totalSellingPriceInPaisa ?: 0,

            maxDeliveryDateTime = DateUtils.getEpoch(maxDeliveryDateTime),
            promisedDeliveryDateTime = DateUtils.getEpoch(promisedDeliveryDateTime),
            deliveredOnDateTime = DateUtils.getEpoch(deliveredOnDateTime),

            // If added to cart from a post
            postId = postId,

            product = product!!.toSaveProductV3Response(),
            productVariant = productVariant!!.toSaveProductVariantV3Response(),
        )
    }
}
