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
data class ProductOrderUpdate(
    val newTotalTaxInPaisa: Long?, // -> INDIRECTLY UPDATED
    val newTotalPriceWithoutTaxInPaisa: Long?, // -> INDIRECTLY UPDATED
    val newTotalPricePayableInPaisa: Long?, // -> INDIRECTLY UPDATED

    val newDeliveryChargeInPaisa: Long?, // -> DIRECTLY UPDATED
    val newAddressId: String?, // -> DIRECTLY UPDATED
    // Cart ID to -> New Count
    val newCartUpdates: Map<String, Long> = emptyMap() // -> DIRECTLY UPDATED
)

fun ProductOrderUpdate.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

fun ProductOrder.getProductOrderUpdate(): ProductOrderUpdate {
    this.apply {
        return jacksonObjectMapper().readValue(productOrderUpdate, ProductOrderUpdate::class.java)
    }
}
