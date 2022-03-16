package com.server.shop.dto

import com.server.common.model.MediaDetailsV2
import com.server.shop.entities.AllProductCategories
import com.server.shop.enums.ProductCategoryV3
import com.server.shop.enums.ProductCategoryV3Group
import com.server.shop.enums.ProductSubCategory
import com.server.shop.enums.ProductVertical

data class AllProductCategoriesResponse (
    val categories: List<ProductCategoryV3Response>
)

data class ProductCategoryV3Response(
    val id: ProductCategoryV3,
    val placementOrder: Int,
    val categoryGroup: ProductCategoryV3Group,
    val displayName: String,
    val subTitle: String,
    val mediaDetails: MediaDetailsV2
)

data class AllProductSubCategoriesResponse (
    val subCategories: List<ProductSubCategoryResponse>
)

data class ProductSubCategoryResponse(
    val id: ProductSubCategory,
    val categories: AllProductCategoriesResponse,
    val placementOrder: Int,
    val displayName: String,
    val subTitle: String,
    val mediaDetails: MediaDetailsV2
)

data class AllProductVerticalsResponse (
    val verticals: List<ProductVerticalResponse>
)

data class ProductVerticalResponse(
    val id: ProductVertical,
    val subCategories: AllProductSubCategoriesResponse,
    val placementOrder: Int,
    val displayName: String,
    val subTitle: String,
    val mediaDetails: MediaDetailsV2
)

fun AllProductCategories.toAllProductCategoriesResponse(): AllProductCategoriesResponse {
    this.apply {
        return AllProductCategoriesResponse(
            categories = categories.map { it.toProductCategoryV3Response() }
        )
    }
}

fun ProductCategoryV3.toProductCategoryV3Response(): ProductCategoryV3Response {
    this.apply {
        return ProductCategoryV3Response(
            id = this,
            placementOrder = placementOrder,
            categoryGroup = categoryGroup,
            displayName = displayName,
            subTitle = subTitle,
            mediaDetails = mediaDetails
        )
    }
}


fun ProductSubCategory.toProductSubCategoryResponse(): ProductSubCategoryResponse {
    this.apply {
        return ProductSubCategoryResponse(
            id = this,
            placementOrder = placementOrder,
            categories = AllProductCategoriesResponse(categories.map { it.toProductCategoryV3Response() }),
            displayName = displayName,
            subTitle = subTitle,
            mediaDetails = mediaDetails
        )
    }
}



fun ProductVertical.toProductVerticalResponse(): ProductVerticalResponse {
    this.apply {
        return ProductVerticalResponse(
            id = this,
            placementOrder = placementOrder,
            subCategories = AllProductSubCategoriesResponse(subCategories.map { it.toProductSubCategoryResponse() }),
            displayName = displayName,
            subTitle = subTitle,
            mediaDetails = mediaDetails
        )
    }
}

