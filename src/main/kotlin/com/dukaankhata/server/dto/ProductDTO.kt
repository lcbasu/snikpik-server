package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Product
import com.dukaankhata.server.entities.ProductVariant
import com.dukaankhata.server.entities.getMediaDetails
import com.dukaankhata.server.enums.ProductUnit
import com.dukaankhata.server.model.MediaDetails
import com.dukaankhata.server.provider.ProductVariantProvider
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class SaveProductVariant(
    val productId: String,
    val variantId: String,
    val variantTitle: String,
    val variantTaxPerUnitInPaisa: Long,
    val variantOriginalPricePerUnitInPaisa: Long,
    val variantSellingPricePerUnitInPaisa: Long,
    val variantTotalUnitInStock: Long,
    var variantMediaDetails: MediaDetails? = null
)

data class SavedProductVariant(
    val productId: String,
    val variantId: String,
    val variantTitle: String,
    val variantTaxPerUnitInPaisa: Long,
    val variantOriginalPricePerUnitInPaisa: Long,
    val variantSellingPricePerUnitInPaisa: Long,
    val variantTotalUnitInStock: Long,
    var variantMediaDetails: MediaDetails? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveProductRequest(
    val companyId: String,
    val mediaDetails: MediaDetails,
    var title: String = "",
    var productUnit: ProductUnit,
    val taxPerUnitInPaisa: Long = 0,
    val originalPricePerUnitInPaisa: Long,
    val sellingPricePerUnitInPaisa: Long,
    val totalUnitInStock: Long = 100,
    var minOrderUnitCount: Long = 1,
    var allProductVariants: List<SaveProductVariant>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedProductResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val mediaDetails: MediaDetails,
    var title: String = "",
    var productUnit: ProductUnit,
    val taxPerUnitInPaisa: Long = 0,
    val originalPricePerUnitInPaisa: Long,
    val sellingPricePerUnitInPaisa: Long,
    val productInStock: Boolean = true, // TODO: Store this as a Seller level input in DB so that they can update this (productInStock) flag
    val totalUnitInStock: Long = 0,
    var minOrderUnitCount: Long = 0,
    var allProductVariants: List<SavedProductVariant>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddProductsToCollectionRequest(
    val companyId: String,
    val collectionId: String,
    val productIds: Set<String>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProductsResponse(
    val products: List<SavedProductResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddProductsToCollectionResponse(
    val company: SavedCompanyResponse,
    val collection: SavedCollectionResponse,
    val products: List<SavedProductResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RelatedProductsResponse(
    val products: List<SavedProductResponse>
)

fun Product.toSavedProductResponse(productVariantProvider: ProductVariantProvider): SavedProductResponse {
    this.apply {
        return SavedProductResponse(
            serverId = id,
            company = company!!.toSavedCompanyResponse(),
            title = title,
            mediaDetails = getMediaDetails(),
            productUnit = productUnit,
            taxPerUnitInPaisa = taxPerUnitInPaisa,
            originalPricePerUnitInPaisa = originalPricePerUnitInPaisa,
            sellingPricePerUnitInPaisa = sellingPricePerUnitInPaisa,
            totalUnitInStock = totalUnitInStock,
            productInStock = totalUnitInStock > 0, // TODO: Store this as a Seller level input in DB so that they can update this (productInStock) flag
            minOrderUnitCount = minOrderUnitCount,
            allProductVariants = productVariantProvider.getProductVariants(this).map { it.toSavedProductVariant() }
        )
    }
}


fun ProductVariant.toSavedProductVariant(): SavedProductVariant {
    this.apply {
        return SavedProductVariant(
            variantId = id,
            productId = product?.id ?: "",
            variantTitle = title,
            variantTaxPerUnitInPaisa = taxPerUnitInPaisa,
            variantOriginalPricePerUnitInPaisa = originalPricePerUnitInPaisa,
            variantSellingPricePerUnitInPaisa = sellingPricePerUnitInPaisa,
            variantTotalUnitInStock = totalUnitInStock,
            variantMediaDetails = getMediaDetails()
        )
    }
}
