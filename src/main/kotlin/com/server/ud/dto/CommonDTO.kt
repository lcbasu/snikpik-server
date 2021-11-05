package com.server.ud.dto

import com.server.dk.model.MediaDetailsV2
import com.server.ud.enums.CategoryV2
import javax.validation.constraints.Max
import javax.validation.constraints.Min


interface LocationResponse {
    val id: String // ID at the city or 5x5 KM square block level (Need to figure out a way)
    val name: String // City Level grouping and naming
    val lat: Double
    val lng:Double
}

data class UserLocationResponse(
    override val id: String,
    override val name: String,
    override val lat: Double,
    override val lng: Double,
): LocationResponse


data class PostLocationResponse(
    override val id: String,
    override val name: String,
    override val lat: Double,
    override val lng: Double,
): LocationResponse

interface PaginationDetails {
    val numFound: Long
    val startIndex: Long
    val endIndex: Long
}

interface FollowingFollower {
    val loggedInUserId: String
    val otherUserId: String
    val followed: Boolean
}

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
