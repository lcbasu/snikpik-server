package com.server.ud.dto

import com.server.dk.model.MediaDetailsV2
import javax.validation.constraints.Max
import javax.validation.constraints.Min


data class LocationResponse (
    val id: String, // ID at the city or 5x5 KM square block level (Need to figure out a way)
    val name: String?, // City Level grouping and naming
    val lat: Double?,
    val lng: Double?,
    val zipcode: String?,
    val googlePlaceId: String?,
)

//data class UserLocationResponse(
//    override val id: String,
//    override val name: String?,
//    override val lat: Double?,
//    override val lng: Double?,
//    override val zipcode: String?,
//    override val googlePlaceId: String?,
//): LocationResponse(id, name, lat, lng, zipcode, googlePlaceId)
//
//
//data class PostLocationResponse(
//    override val id: String,
//    override val name: String?,
//    override val lat: Double?,
//    override val lng: Double?,
//    override val zipcode: String?,
//): LocationResponse(id, name, lat, lng, zipcode)

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

data class ResourceLikesReportDetailForUser(
    val userId: String,
    val liked: Boolean
)

data class ResourceLikesReportDetail(
    val resourceId: String,
    val likes: Long,
    val userLevelInfo: ResourceLikesReportDetailForUser
)

data class BookmarkReportDetailForUser(
    val userId: String,
    val bookmarked: Boolean
)

data class BookmarkReportDetail(
    val resourceId: String,
    val bookmarks: Long,
    val userLevelInfo: BookmarkReportDetailForUser
)

// Never request for more than page size of 100
// Even though ES limits pagination to 10K results.
// But that is the extreme case
// https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#paginate-search-results
open class PaginationSearchRequest (
    open val typedText: String,
    open val from: Int = 10,
    @Min(1)
    @Max(100)
    open val size: Int = 10,
)

open class PaginationSearchResponse (
    open val typedText: String,
    open val from: Int,
    open val size: Int,
    open val numFound: Long
)