package com.dukaankhata.server.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedActiveDiscountsResponse(
    val company: SavedCompanyResponse,
    val discounts: List<SavedDiscountResponse>,
)

// Arrange this accordingly on the home page
// Sending all data so that searching is easier
@JsonIgnoreProperties(ignoreUnknown = true)
data class ShopViewForCustomerResponse(
    val user: SavedUserResponse? = null,
    val company: SavedCompanyResponse,

    // Using this as any product has to always be part of a collection
    // So in one DTO we will have all the details
    val allCollectionsWithProducts: AllCollectionsWithProductsResponse,
    val bestsellerProductsIds: Set<String>,
    val pastOrderedProductsIds: Set<String>,
    val bestsellerCollectionsIds: Set<String>,
    val collectionsIdsOrderedFromInPast: Set<String>,
)

data class ProductCollectionResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val collection: SavedCollectionResponse,
    val product: SavedProductResponse
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ShopCompleteDataResponse(
    val company: SavedCompanyResponse,
    val products: List<SavedProductResponse>,
    val collections: List<SavedCollectionResponse>,
    val productCollections: List<ProductCollectionResponse>,
)
