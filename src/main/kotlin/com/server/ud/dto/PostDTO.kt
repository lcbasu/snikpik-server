package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.DateUtils
import com.server.common.model.MediaDetailsV2
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getCategories
import com.server.ud.entities.post.getHashTags
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakePostRequest(
    val countOfPost: Int,
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedPostResponse(
    val postId: String? = null,
    val postType: PostType,
    val userId: String? = null,
    val locationId: String? = null,
    val googlePlaceId: String? = null,
    val zipcode: String? = null,
    val locationName: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val createdAt: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val tags: AllHashTags = AllHashTags(emptySet()),
    val categories: AllCategoryV2Response = AllCategoryV2Response(emptyList()),
    val mediaDetails: MediaDetailsV2? = null,
)

fun Post.toSavedUserPostResponse(): SavedPostResponse {
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
            createdAt = DateUtils.getEpoch(createdAt),
            title = title,
            description = description,
            tags = getHashTags(),
            categories = getCategories(),
            mediaDetails = getMediaDetails(),
        )
    }
}
