package com.server.ud.dto

import com.server.common.dto.ProfileTypeResponse
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.post.NearbyVideoPostsByZipcode
import com.server.ud.entities.post.getHashTags
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.entities.user.getProfiles
import com.server.ud.model.AllHashTags

// VideoFeedView -> VFV

data class VideoFeedViewSinglePostDetail(
    val tags: AllHashTags,
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

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
    val posts: List<VideoFeedViewSinglePostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

fun NearbyVideoPostsByZipcode.toVideoFeedViewSinglePostDetail(): VideoFeedViewSinglePostDetail {
    this.apply {
        return VideoFeedViewSinglePostDetail(
            postId = postId,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            media = getMediaDetails(),
            title = title,
            tags = getHashTags(),
            description = description
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
