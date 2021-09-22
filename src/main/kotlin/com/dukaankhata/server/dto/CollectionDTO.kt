package com.dukaankhata.server.dto

import AllCollectionsWithProductsRaw
import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.entities.getMediaDetails
import com.dukaankhata.server.model.MediaDetails
import com.dukaankhata.server.provider.ProductCollectionProvider
import com.dukaankhata.server.provider.ProductVariantProvider
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCollectionRequest(
    val companyId: String,
    var title: String,
    var subTitle: String?,
    val mediaDetails: MediaDetails,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCollectionResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    var title: String,
    var subTitle: String?,
    val mediaDetails: MediaDetails,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllCollectionsResponse(
    val collections: List<SavedCollectionResponse>
)

data class CollectionWithProductsResponse(
    val collection: SavedCollectionResponse,
    val products: List<SavedProductResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllCollectionsWithProductsResponse(
    val collectionsWithProducts: List<CollectionWithProductsResponse>
)

fun Collection.toSavedCollectionResponse(): SavedCollectionResponse {
    this.apply {
        return SavedCollectionResponse(
            serverId = id,
            company = company!!.toSavedCompanyResponse(),
            title = title,
            subTitle = subTitle,
            mediaDetails = getMediaDetails()
        )
    }
}

fun AllCollectionsWithProductsRaw.toAllCollectionsWithProductsResponse(productVariantProvider: ProductVariantProvider, productCollectionProvider: ProductCollectionProvider): AllCollectionsWithProductsResponse {
    this.apply {
        return AllCollectionsWithProductsResponse(
            collectionsWithProducts = collectionsWithProducts.map {
                CollectionWithProductsResponse(
                    collection = it.collection.toSavedCollectionResponse(),
                    products = it.products.map {
                        it.toSavedProductResponse(productVariantProvider, productCollectionProvider)
                    }
                )
            }
        )
    }
}
