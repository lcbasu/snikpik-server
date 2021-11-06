package com.server.ud.dto

import com.server.dk.model.MediaDetailsV2
import javax.validation.constraints.Max
import javax.validation.constraints.Min


open class LocationResponse (
    open val id: String, // ID at the city or 5x5 KM square block level (Need to figure out a way)
    open val name: String?, // City Level grouping and naming
    open val lat: Double?,
    open val lng: Double?,
    open val zipcode: String?,
)

data class UserLocationResponse(
    override val id: String,
    override val name: String?,
    override val lat: Double?,
    override val lng: Double?,
    override val zipcode: String?,
): LocationResponse(id, name, lat, lng, zipcode)


data class PostLocationResponse(
    override val id: String,
    override val name: String?,
    override val lat: Double?,
    override val lng: Double?,
    override val zipcode: String?,
): LocationResponse(id, name, lat, lng, zipcode)

interface PaginationDetails {
    val numFound: Long
    val startIndex: Long
    val endIndex: Long
}

data class FollowingFollowerResponse (
    val loggedInUserId: String,
    val otherUserId: String,
    val followed: Boolean,
)

interface PostMiniDetail{
    val postId: String
    val userId: String
    val media: MediaDetailsV2?
    val title: String?
}

open class PaginationRequest (
    @Min(1)
    @Max(1000)
    open val limit: Int = 10,
    open val pagingState: String? = null,
)

open class PaginationResponse (
    open val count: Int? = null,
    open val pagingState: String? = null,
    open val hasNext: Boolean? = null
)

data class ResourceLikesDetailForUser(
    val userId: String,
    val liked: Boolean
)

data class ResourceLikesDetail(
    val resourceId: String,
    val likes: Long,
    val userLevelInfo: ResourceLikesDetailForUser
)

data class ResourceSavesDetailForUser(
    val resourceId: String,
    val userId: String,
    val saves: Long,
    val saved: Boolean
)
