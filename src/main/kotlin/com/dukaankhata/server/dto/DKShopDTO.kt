package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.TakeShopOnlineAfter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true)
data class UsernameAvailableResponse(
    val available: Boolean = false,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveUsernameRequest(
    val companyId: Long,
    val username: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveUsernameResponse(
    val available: Boolean = false,
    val company: SavedCompanyResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOfflineRequest(
    val companyId: Long,
    val takeShopOnlineAfter: TakeShopOnlineAfter,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOfflineResponse(
    val takeShopOnlineAfter: TakeShopOnlineAfter? = null,
    val company: SavedCompanyResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyAddressRequest(
    val companyId: Long,
    val name: String = "",
    val address: SaveAddressRequest,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCompanyAddressResponse(
    val company: SavedCompanyResponse,
    val address: SavedAddressResponse,
)

// Arrange this accordingly on the home page
// Sending all data so that searching is easier
@JsonIgnoreProperties(ignoreUnknown = true)
data class ShopViewForCustomerResponse(
    val company: SavedCompanyResponse,
    val allProducts: List<SavedProductResponse>,
    val bestsellerProductsIds: Set<String>,
    val pastOrderedProductsIds: Set<String>,
    val allCollections: List<SavedCollectionResponse>,
    val bestsellerCollectionsIds: Set<String>,
    val collectionsIdsOrderedFromInPast: Set<String>,
)
