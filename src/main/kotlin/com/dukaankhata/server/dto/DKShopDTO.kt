package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.TakeShopOnlineAfter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true)
data class UsernameAvailableResponse(
    val available: Boolean = false,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveUsernameRequest(
    val companyId: String,
    val username: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveUsernameResponse(
    val available: Boolean = false,
    val company: SavedCompanyResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOnlineNowRequest(
    val companyId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOfflineRequest(
    val companyId: String,
    val takeShopOnlineAfter: TakeShopOnlineAfter,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOfflineResponse(
    val takeShopOnlineAfter: TakeShopOnlineAfter? = null,
    val company: SavedCompanyResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyAddressRequest(
    val companyId: String,
    val name: String = "",
    val address: SaveAddressRequest,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCompanyAddressResponse(
    val company: SavedCompanyResponse,
    val address: SavedAddressResponse,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedActiveDiscountsResponse(
    val company: SavedCompanyResponse,
    val discounts: List<SavedDiscountResponse>,
)

// Arrange this accordingly on the home page
// Sending all data so that searching is easier
@JsonIgnoreProperties(ignoreUnknown = true)
data class ShopViewForCustomerResponse(
    val user: SavedUserResponse,
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
