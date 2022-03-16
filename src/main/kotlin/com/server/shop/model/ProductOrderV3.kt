package com.server.shop.model

import com.server.shop.entities.CartItemV3
import com.server.shop.entities.ProductOrderV3
import com.server.shop.enums.OrderPaymentModeV3

data class UpdatedCartDataV3 (
    val updatedCartItem: CartItemV3,
    val updatedProductOrder: ProductOrderV3,
    val productOrderCartItems: List<CartItemV3>
)

data class ProductOrderStateChangeDataV3(

    val addressId: String? = null,

    val cartItems: Map<String, Long> = emptyMap(),
    val deliveryChargeInPaisa: Long = 0,

    val totalTaxInPaisa: Long  = 0,
    val totalPriceWithoutTaxInPaisa: Long = 0,
    val totalPricePayableInPaisa: Long = 0,

    var discountInPaisa: Long = 0,
    val discountId: String? = null,

    var productOrderStateBeforeUpdate: String = "",

    var paymentMode: OrderPaymentModeV3 = OrderPaymentModeV3.NONE,
    var successPaymentId: String? = "",
)

data class OrderStateTransitionOutputV3(
    val transitionPossible: Boolean = false,
    val errorMessage: String = ""
)
