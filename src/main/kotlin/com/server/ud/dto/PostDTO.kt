package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.dto.AllCategoryV2Response
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.post.*
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.InstagramMediaType
import com.server.ud.enums.PostReportActionType
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags
import java.time.Instant

data class AllPostsForDateResponse(
    val forDate: String,
    val posts: List<SavedPostResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class TotalPostsForDateResponse(
    val forDate: String,
    val posts: List<SavedPostResponse>,
)

data class AllPostsForDateRequest (
    val forDate: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class PostsByUserPostDetail(
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

data class PostsByUserResponse(
    val posts: List<PostsByUserPostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class PostsByUserResponseV2(
    val posts: List<SavedPostResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class PostsByUserRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class PostsByUserRequestV2 (
    val userIdOrHandle: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PostReportRequest (
    val reportedByUserId: String,
    val postId: String,
    val reason: String?,
    val action: PostReportActionType,
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PostReportResponse (
    val reportedByUserId: String,
    val postId: String,
    val actionDetails: String,
    val reason: String?,
    val action: PostReportActionType,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllPostReportResponse (
    val reports: List<PostReportResponse>
)

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

    // For products to sell
    val taggedProductIds: Set<String>? = emptySet(),

    // Just in case it is passed, use it
    var createdAt: Instant? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdatePostRequest(
    val postId: String,
    val title: String? = null,
    val description: String? = null,
    val tags: Set<String>? = null,
    val categories: Set<CategoryV2>? = null,
    val mediaDetails: MediaDetailsV2? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstagramPagingCursor(
    val before: String,
    val after: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstagramPaging(
    val cursors: InstagramPagingCursor? = null,
    val next: String? = null,
    val previous: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstagramPostsPaginatedResponse(
    val data: List<InstagramPostResponse> = emptyList(),
    val paging: InstagramPaging? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstagramPostChildrenResponse(
    val data: List<InstagramPostResponse> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstagramPostResponse(
    val id: String,
    @JsonProperty("media_type")
    var mediaType: InstagramMediaType,

    var caption: String? = null,
    @JsonProperty("media_url")
    var mediaUrl: String,

    @JsonProperty("thumbnail_url")
    var thumbnailUrl: String? = null,
    var permalink: String? = null,

    // "2022-02-13T11:56:15+0000"
    var timestamp: String? = null,
    var username: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstagramUserInfoResponse(
    val id: String,
    var username: String,

    @JsonProperty("account_type")
    var accountType: String? = null,
)

fun InstagramPostChildrenResponse?.convertToString(): String {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            ""
        }
    }
}

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

fun PostsByDate.toSavedPostResponse(): SavedPostResponse {
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
