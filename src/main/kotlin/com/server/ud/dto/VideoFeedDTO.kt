package com.server.ud.dto

import com.server.common.dto.PaginationResponse
import com.server.common.dto.ProfileTypeResponse
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.post.*
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.entities.user.getProfiles

// VideoFeedView -> VFV

//data class VideoFeedViewSinglePostDetail(
//    val tags: AllHashTags,
//    override val postId: String,
//    override val userId: String,
//    override val createdAt: Long,
//    override val media: MediaDetailsV2?,
//    override val title: String?,
//    override val description: String?
//): PostCommonDetail

data class VideoFeedViewSingleUserDetail(
    val userId: String,
    val handle: String?,
    val verified: Boolean,
    val dp: MediaDetailsV2?,
    val profileTypeToShow: ProfileTypeResponse?,
    val location: LocationResponse?,
)

//data class VideoFeedViewFollowDetail(
//    override val loggedInUserId: String,
//    override val otherUserId: String,
//    override val followed: Boolean
//): FollowingFollower(loggedInUserId, otherUserId, followed)

//data class VideoFeedViewSingleLikesDetail(
//    val postId: String,
//    val likes: Long,
//    val liked: Boolean,
//)

//data class VideoFeedViewSingleCommentsDetail(
//    val postId: String,
//    val comments: Long,
//    val commented: Boolean,
//)
//
//data class VideoFeedViewSingleSaveDetail(
//    val postId: String,
//    val saves: Long,
//    val saved: Boolean,
//)

data class VideoFeedViewResultList(
    val posts: List<SavedPostResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

fun NearbyVideoPostsByZipcode.toSavedPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            media = getMediaDetails(),
            sourceMediaDetails = getSourceMediaDetails(),
            title = title,
            tags = getHashTags(),
            description = description,
            postType = postType,
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

fun UserV2.toVideoFeedViewSingleUserDetail(): VideoFeedViewSingleUserDetail {
    this.apply {
        return VideoFeedViewSingleUserDetail(
            userId = userId,
            handle = handle,
            verified = verified,
            dp = getMediaDetailsForDP(),
            profileTypeToShow = getProfiles().profileTypes.firstOrNull(),
            location = permanentLocationId?.let {
                LocationResponse(
                    id = permanentLocationId,
                    name = permanentLocationName,
                    lat = permanentLocationLat,
                    lng = permanentLocationLng,
                    zipcode = permanentLocationZipcode,
                    googlePlaceId = permanentGooglePlaceId,
                )
            },
        )
    }
}
