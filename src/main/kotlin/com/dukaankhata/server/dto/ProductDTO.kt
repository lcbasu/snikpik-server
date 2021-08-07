package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.ProductUnit
import com.dukaankhata.server.model.MediaDetails
import com.dukaankhata.server.provider.ProductVariantProvider
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class VariantInfo(
    var title: String?, // Like 'Red' in case of color and 'Medium' in case of size
    var code: String?, // Like '#ffffff' in case of color or 'M' in case of size
    var image: String? // Image to represent color or size
)

fun VariantInfo.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

data class SaveProductVariant(
    val variantTitle: String,
    val variantTaxPerUnitInPaisa: Long,
    val variantOriginalPricePerUnitInPaisa: Long,
    val variantSellingPricePerUnitInPaisa: Long,
    val variantTotalUnitInStock: Long,
    var variantMediaDetails: MediaDetails?,
    var variantColorInfo: VariantInfo?,
    var variantSizeInfo: VariantInfo?
)

data class SavedProductVariant(
    val productId: String,
    val variantId: String,
    val variantTitle: String,
    val variantTaxPerUnitInPaisa: Long,
    val variantOriginalPricePerUnitInPaisa: Long,
    val variantSellingPricePerUnitInPaisa: Long,
    val variantTotalUnitInStock: Long,
    var variantMediaDetails: MediaDetails? = null,
    var variantColorInfo: VariantInfo?,
    var variantSizeInfo: VariantInfo?
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
data class ProductUnitResponse(
    val unitType: ProductUnit,
    val rank: Int,
    val displayName: String,
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProductUnits(
    val productUnits: List<ProductUnitResponse>
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
            variantMediaDetails = getMediaDetails(),
            variantColorInfo = getColorInfo(),
            variantSizeInfo = getSizeInfo()
        )
    }
}
