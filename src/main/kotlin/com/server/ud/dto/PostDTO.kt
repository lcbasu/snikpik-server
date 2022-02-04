package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.post.*
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakePostRequest(
    val countOfPost: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeletePostRequest(
    val postId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavePostRequest(
    val postType: PostType,
    val title: String? = null,
    val description: String? = null,
    val tags: Set<String> = emptySet(),
    val categories: Set<CategoryV2> = emptySet(),
    val locationRequest: SaveLocationRequest? = null,
    val mediaDetails: MediaDetailsV2? = null,
)

//@JsonIgnoreProperties(ignoreUnknown = true)
//data class PostCommonDetail (
//    val postId: String,
//    val postType: PostType,
//    val userId: String,
//    val createdAt: Long,
//    val media: MediaDetailsV2?,
//    val title: String?,
//    val description: String?,
//    val tags: AllHashTags,
//    val locationId: String?,
//    val zipcode: String?,
//    val locationName: String?,
//    val city: String?,
//    val categories: AllCategoryV2Response,
//)

interface PostMiniDetail{
    val postId: String
    val userId: String
    val createdAt: Long
    val media: MediaDetailsV2?
    val title: String?
    val description: String?
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedPostResponse(
    val postId: String,
    val postType: PostType,
    val userId: String,
    val createdAt: Long,
    val locationId: String? = null,
    val googlePlaceId: String? = null,
    val zipcode: String? = null,
    val locationName: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val route: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
    val completeAddress: String? = null,
    val title: String? = null,
    val description: String? = null,
    val tags: AllHashTags = AllHashTags(emptySet()),
    val categories: AllCategoryV2Response = AllCategoryV2Response(emptyList()),
    val mediaDetails: MediaDetailsV2? = null,

    // For backward compatibility
    val media: MediaDetailsV2? = null,

    // To use the actual media urls wherever required
    val sourceMediaDetails: MediaDetailsV2? = null,
)

fun Post.toSavedPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            postType = postType,
            userId = userId,
            locationId = locationId,
            zipcode = zipcode,
            googlePlaceId = googlePlaceId,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            city = city,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
            createdAt = DateUtils.getEpoch(createdAt),
            title = title,
            description = description,
            tags = getHashTags(),
            categories = getCategories(),
            mediaDetails = getMediaDetails(),
            media = getMediaDetails(),
            sourceMediaDetails = getSourceMediaDetails(),
        )
    }
}
