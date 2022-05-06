package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.CommonUtils
import com.server.ud.entities.view.ResourceViewsCountByResource
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

data class ResourceViewsReportDetailForUser(
    val userId: String,
    val viewed: Boolean
)

data class ResourceViewsReportDetail(
    val resourceId: String,
    val views: Long,

    // This can be null for cases when the user is not logged in (Guest/Anonymous Users)
    val userLevelInfo: ResourceViewsReportDetailForUser?
)

data class ResourceViewsCountResponse(
    val resourceId: String,
    val views: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveResourceViewRequest(
    val resourceId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResourceViewRequest(
    val userId: String,
    val resourceId: String,
)

fun String.toResourceViewRequest(): ResourceViewRequest {
    this.apply {
        val split = this.split(CommonUtils.STRING_SEPARATOR)
        return ResourceViewRequest(
            userId = split[0],
            resourceId = split[1],
        )
    }
}

fun ResourceViewsCountByResource.toResourceViewsCountResponse(): ResourceViewsCountResponse {
    this.apply {
        return ResourceViewsCountResponse(
            resourceId = resourceId ?: "",
            views = viewsCount ?: 0,
        )
    }
}

fun ResourceViewRequest.toResourceViewId(): String {
    this.apply {
        return "${userId}${CommonUtils.STRING_SEPARATOR}${resourceId}"
    }
}

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
