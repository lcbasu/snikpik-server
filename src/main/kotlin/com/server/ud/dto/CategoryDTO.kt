package com.server.ud.dto

import com.server.dk.model.MediaDetails
import com.server.ud.enums.CategoryGroupV2
import com.server.ud.enums.CategoryV2

data class AllCategoryGroupV2Response(
    val groups: List<CategoryGroupV2Response>
)

data class CategoryGroupV2Response (
    val id: CategoryGroupV2,
    val displayName: String,
    val mediaDetails: MediaDetails
)

data class AllCategoryV2Response(
    val categories: List<CategoryV2Response>
)

data class CategoryV2Response(
    val id: CategoryV2,
    val categoryGroup: CategoryGroupV2,
    val displayName: String,
    val mediaDetails: MediaDetails
)

fun CategoryV2.toCategoryV2Response(): CategoryV2Response {
    this.apply {
        return CategoryV2Response(
            id = this,
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
