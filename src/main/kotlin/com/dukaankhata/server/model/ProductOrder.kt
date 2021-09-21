package com.dukaankhata.server.model

import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.entities.ProductOrderStateChange
import com.dukaankhata.server.enums.OrderPaymentMode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class OrderStateTransitionOutput(
    val transitionPossible: Boolean = false,
    val errorMessage: String = ""
)

// To keep a snapshot of ProductOrder entity inside ProductOrderStateChange entity
data class ProductOrderStateChangeData(

    val addressId: String? = null,

    val cartItems: Map<String, Long>,
    val deliveryChargeInPaisa: Long,

    val totalTaxInPaisa: Long,
    val totalPriceWithoutTaxInPaisa: Long,
    val totalPricePayableInPaisa: Long,

    var discountInPaisa: Long = 0,
    val discountId: String? = null,

    var productOrderStateBeforeUpdate: String = "",

    var paymentMode: OrderPaymentMode = OrderPaymentMode.NONE,
    var successPaymentId: String? = "",
)


fun ProductOrderStateChangeData.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

fun ProductOrderStateChange.getProductOrderStateChangeData(): ProductOrderStateChangeData? {
    this.apply {
        return try {
            if (productOrderStateChangeData.isNotBlank()) {
                jacksonObjectMapper().readValue(productOrderStateChangeData, ProductOrderStateChangeData::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


/**
 *
 * Any of the fields will have value ONLY AND ONLY IF
 *
 * they have been updated, otherwise it will be null
 *
 * */
data class ProductOrderStateBeforeUpdate(

    val addressId: String? = null,

    val cartItems: Map<String, Long>,
    val deliveryChargeInPaisa: Long,

    val totalTaxInPaisa: Long,
    val totalPriceWithoutTaxInPaisa: Long,
    val totalPricePayableInPaisa: Long,
)

fun ProductOrderStateBeforeUpdate.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

fun ProductOrder.getProductOrderStateBeforeUpdate(): ProductOrderStateBeforeUpdate? {
    this.apply {
        return try {
            if (productOrderStateBeforeUpdate.isNotBlank()) {
                jacksonObjectMapper().readValue(productOrderStateBeforeUpdate, ProductOrderStateBeforeUpdate::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
