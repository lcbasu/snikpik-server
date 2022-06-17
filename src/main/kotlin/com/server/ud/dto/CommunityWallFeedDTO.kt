package com.server.ud.dto

import com.server.common.dto.PaginationRequest
import com.server.common.dto.PaginationResponse
import com.server.common.dto.ProfileTypeResponse
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.post.*
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.entities.user.getProfiles

//data class CommunityWallViewPostDetail(
//    override val postId: String,
//    override val userId: String,
//    override val createdAt: Long,
//    override val media: MediaDetailsV2?,
//    override val title: String?,
//    override val description: String?
//): PostCommonDetail

data class CommunityWallViewUserDetail(
    val userId: String,
    val handle: String?,
    val name: String?,
    val dp: MediaDetailsV2?,
    val verified: Boolean?,
    val profileTypeToShow: ProfileTypeResponse?,
)

data class CommunityWallViewResponse(
    val posts: List<SavedPostResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)


data class CommunityWallFeedRequest (
    val zipcode: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)


fun NearbyPostsByZipcode.toSavedPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            media = getMediaDetails(),
            sourceMediaDetails = getSourceMediaDetails(),
            title = title,
            description = description,
            postType = postType,
            tags = getHashTags(),
            locationId = locationId,
            zipcode = zipcode,
            locationName = locationName,
            city = city,
            categories = getCategories(),
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
            mediaDetails = getMediaDetails(),
        )
    }
}


fun PostsByPostType.toSavedPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            media = getMediaDetails(),
            sourceMediaDetails = getSourceMediaDetails(),
            title = title,
            description = description,
            postType = postType,
            tags = getHashTags(),
            locationId = locationId,
            zipcode = zipcode,
            locationName = locationName,
            city = city,
            categories = getCategories(),
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
            mediaDetails = getMediaDetails(),
        )
    }
}

fun UserV2.toCommunityWallViewUserDetail(): CommunityWallViewUserDetail {
    this.apply {
        return CommunityWallViewUserDetail(
            userId = userId,
            handle = handle,
            name = fullName,
            verified = verified,
            dp = getMediaDetailsForDP(),
            profileTypeToShow = getProfiles().profileTypes.firstOrNull(),
        )
    }
}
