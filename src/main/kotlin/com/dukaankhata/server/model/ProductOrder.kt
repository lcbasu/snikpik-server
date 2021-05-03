package com.dukaankhata.server.model

import com.dukaankhata.server.entities.ProductOrder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class OrderStateTransitionOutput(
    val transitionPossible: Boolean = false,
    val errorMessage: String = ""
)

/**
 *
 * Any of the fields will have value ONLY AND ONLY IF
 *
 * they have been updated, otherwise it will be null
 *
 * */
data class ProductOrderStateBeforeUpdate(

    val addressId: String,

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
