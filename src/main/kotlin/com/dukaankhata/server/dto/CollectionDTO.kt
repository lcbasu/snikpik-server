package com.dukaankhata.server.dto

import AllCollectionsWithProductsRaw
import CollectionWithProductsRaw
import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.entities.getMediaDetails
import com.dukaankhata.server.model.MediaDetails
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
    var totalOrderAmountInPaisa: Long,
    var totalViewsCount: Long,
    var totalClicksCount: Long,
    var totalOrdersCount: Long,
    var totalProductsViewCount: Long,
    var totalProductsClickCount: Long,
    var totalUnitsOrdersCount: Long
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
            mediaDetails = getMediaDetails(),
            totalOrderAmountInPaisa = totalOrderAmountInPaisa ?: 0,
            totalViewsCount = totalViewsCount ?: 0,
            totalClicksCount = totalClicksCount ?: 0,
            totalOrdersCount = totalOrdersCount ?: 0,
            totalProductsViewCount = totalProductsViewCount ?: 0,
            totalProductsClickCount = totalProductsClickCount ?: 0,
            totalUnitsOrdersCount = totalUnitsOrdersCount ?: 0,
        )
    }
}

fun CollectionWithProductsRaw.toCollectionWithProductsResponse(): CollectionWithProductsResponse {
    this.apply {
        return CollectionWithProductsResponse(
            collection = collection.toSavedCollectionResponse(),
            products = products.map {
                it.toSavedProductResponse()
            }
        )
    }
}

fun AllCollectionsWithProductsRaw.toAllCollectionsWithProductsResponse(): AllCollectionsWithProductsResponse {
    this.apply {
        return AllCollectionsWithProductsResponse(
            collectionsWithProducts = collectionsWithProducts.map {
                it.toCollectionWithProductsResponse()
            }
        )
    }
}
