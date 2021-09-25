package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.CartItem
import com.dukaankhata.server.enums.CartItemUpdateAction
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCartRequest(
    val productVariantId: String,

    // either action or newQuantity has to be present
    val action: CartItemUpdateAction? = null,
    val newQuantity: Long? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCartItemResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val totalUnits: Long = 0,
    val taxPerUnitInPaisa: Long = 0,
    val pricePerUnitInPaisa: Long = 0,
    val totalTaxInPaisa: Long = 0,
    val totalPriceWithoutTaxInPaisa: Long = 0,
    val orderId: String?,
    val product: SavedProductResponse?,
    val productVariant: SavedProductVariantResponse?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MigrateCartRequest(
    val fromUserId: String,
    val toUserId: String,
)

fun CartItem.toSavedCartItemResponse(): SavedCartItemResponse {
    this.apply {
        return SavedCartItemResponse(
            serverId = id.toString(),
            company = company!!.toSavedCompanyResponse(),
            totalUnits = totalUnits,
            taxPerUnitInPaisa = taxPerUnitInPaisa,
            pricePerUnitInPaisa = pricePerUnitInPaisa,
            totalTaxInPaisa = totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa,
            orderId = productOrder?.id,
            // This is a fix for older orders where we were not saving the product id for cart
            product = product?.toSavedProductResponse() ?: productVariant?.product?.toSavedProductResponse(),
            productVariant = productVariant?.toSavedProductVariant()
        )
    }
}
