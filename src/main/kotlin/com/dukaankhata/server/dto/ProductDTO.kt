package com.dukaankhata.server.dto

import VariantInfos
import com.dukaankhata.server.entities.Product
import com.dukaankhata.server.entities.ProductVariant
import com.dukaankhata.server.entities.getMediaDetails
import com.dukaankhata.server.entities.getVariantInfos
import com.dukaankhata.server.enums.ProductUnit
import com.dukaankhata.server.enums.toProductUnitResponse
import com.dukaankhata.server.model.MediaDetails
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 *
 * Like Medium Red variant of a Shirt
 * Here:
 * Medium -> is a VariantInfo of type SIZE
 * Red -> is a VariantInfo of type COLOR
 * Shirt -> is a Product
 *
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveProductVariantRequest(
    val variantTitle: String?,
    val variantTaxPerUnitInPaisa: Long?,
    val variantOriginalPricePerUnitInPaisa: Long?,
    val variantSellingPricePerUnitInPaisa: Long?,
    val variantTotalUnitInStock: Long?,
    var variantMediaDetails: MediaDetails?,
    var variantInfos: VariantInfos,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedProductVariantResponse(
    val productId: String,
    val variantId: String,
    val variantTitle: String,
    val variantTaxPerUnitInPaisa: Long,
    val variantOriginalPricePerUnitInPaisa: Long,
    val variantSellingPricePerUnitInPaisa: Long,
    val variantTotalUnitInStock: Long,
    var variantMediaDetails: MediaDetails? = null,
    var variantInfos: VariantInfos?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveProductRequest(
    val companyId: String,
    val collectionsIds: Set<String> = emptySet(),
    val mediaDetails: MediaDetails,
    var title: String = "",
    var productUnit: ProductUnit,
    val unitQuantity: Long = 0,
    val description: String = "",
    val taxPerUnitInPaisa: Long = 0,
    val originalPricePerUnitInPaisa: Long,
    val sellingPricePerUnitInPaisa: Long,
    val totalUnitInStock: Long = 100,
    var minOrderUnitCount: Long = 1,
    var allProductVariants: List<SaveProductVariantRequest> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedProductResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val mediaDetails: MediaDetails,
    var title: String = "",
    var productUnit: ProductUnitResponse,
    val unitQuantity: Long = 0,
    val description: String = "",
    val taxPerUnitInPaisa: Long = 0,
    val originalPricePerUnitInPaisa: Long,
    val sellingPricePerUnitInPaisa: Long,
    val productInStock: Boolean = true, // TODO: Store this as a Seller level input in DB so that they can update this (productInStock) flag
    val totalUnitInStock: Long = 0,
    var minOrderUnitCount: Long = 0,
    var allProductVariants: List<SavedProductVariantResponse> = emptyList(),
    val collections: List<SavedCollectionResponse> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddProductsToCollectionRequest(
    val companyId: String,
    val collectionId: String,
    val productIds: Set<String>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddProductToCollectionsRequest(
    val companyId: String,
    val collectionsIds: Set<String>,
    val productId: String
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class AddProductToCollectionsResponse(
    val company: SavedCompanyResponse,
    val collections: List<SavedCollectionResponse>,
    val product: SavedProductResponse
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

fun Product.toSavedProductResponse(): SavedProductResponse {
    this.apply {
        return SavedProductResponse(
            serverId = id,
            company = company!!.toSavedCompanyResponse(),
            title = title,
            mediaDetails = getMediaDetails(),
            productUnit = productUnit.toProductUnitResponse(),
            unitQuantity = unitQuantity,
            description = description ?: "",
            taxPerUnitInPaisa = taxPerUnitInPaisa,
            originalPricePerUnitInPaisa = originalPricePerUnitInPaisa,
            sellingPricePerUnitInPaisa = sellingPricePerUnitInPaisa,
            totalUnitInStock = totalUnitInStock,
            productInStock = totalUnitInStock > 0, // TODO: Store this as a Seller level input in DB so that they can update this (productInStock) flag
            minOrderUnitCount = minOrderUnitCount,
            allProductVariants = productVariants.map { it.toSavedProductVariant() },
            collections = productCollections.mapNotNull { it.collection?.toSavedCollectionResponse() }
        )
    }
}

fun ProductVariant.toSavedProductVariant(): SavedProductVariantResponse {
    this.apply {
        return SavedProductVariantResponse(
            variantId = id,
            productId = product?.id ?: "",
            variantTitle = title,
            variantTaxPerUnitInPaisa = taxPerUnitInPaisa,
            variantOriginalPricePerUnitInPaisa = originalPricePerUnitInPaisa,
            variantSellingPricePerUnitInPaisa = sellingPricePerUnitInPaisa,
            variantTotalUnitInStock = totalUnitInStock,
            variantMediaDetails = getMediaDetails(),
            variantInfos = getVariantInfos()
        )
    }
}
