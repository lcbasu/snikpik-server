package com.server.ud.dto

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.MediaDetailsV2
import com.server.ud.enums.CategoryGroupV2
import com.server.ud.enums.CategoryV2

data class AllCategoryGroupV2Response(
    val groups: List<CategoryGroupV2Response>
)

data class CategoryGroupV2Response (
    val id: CategoryGroupV2,
    val displayName: String,
    val mediaDetails: MediaDetailsV2
)

data class AllCategoryV2Response(
    val categories: List<CategoryV2Response>
)

data class CategoryV2Response(
    val id: CategoryV2,
    val placementOrder: Int,
    val categoryGroup: CategoryGroupV2,
    val displayName: String,
    val mediaDetails: MediaDetailsV2
)

fun getCategories(categories: String?): AllCategoryV2Response {
    return try {
        jacksonObjectMapper().readValue(categories, AllCategoryV2Response::class.java)
    } catch (e: Exception) {
        AllCategoryV2Response(emptyList())
    }
}

fun AllCategoryV2Response.convertToString(): String? {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            null
        }
    }
}

fun CategoryV2.toCategoryV2Response(): CategoryV2Response {
    this.apply {
        return CategoryV2Response(
            id = this,
            placementOrder = placementOrder,
            categoryGroup = categoryGroup,
            displayName = displayName,
            mediaDetails = mediaDetails,
        )
    }
}

fun CategoryGroupV2.toCategoryGroupV2Response(): CategoryGroupV2Response {
    this.apply {
        return CategoryGroupV2Response(
            id = this,
            displayName = displayName,
            mediaDetails = mediaDetails,
        )
    }
}
