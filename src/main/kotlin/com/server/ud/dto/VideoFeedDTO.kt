package com.server.ud.dto

import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.post.NearbyPostsByZipcode
import com.server.ud.entities.post.getHashTags
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.enums.UserProfession
import com.server.ud.model.HashTagsList

// VideoFeedView -> VFV

data class VideoFeedViewSinglePostDetail(
    val postId: String,
    val userId: String,
    val media: MediaDetailsV2?,
    val title: String?,
    val tags: HashTagsList
)

data class VideoFeedViewSingleUserDetail(
    val userId: String,
    val handle: String?,
    val verified: Boolean,
    val dp: MediaDetailsV2?,
    val profession: UserProfession?,
    val location: UserLocationResponse?,
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

data class VideoFeedViewSingleCommentsDetail(
    val postId: String,
    val comments: Long,
    val commented: Boolean,
)

data class VideoFeedViewSingleSaveDetail(
    val postId: String,
    val saves: Long,
    val saved: Boolean,
)

data class VideoFeedViewResultList(
    val posts: List<VideoFeedViewSinglePostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

fun NearbyPostsByZipcode.toVideoFeedViewSinglePostDetail(): VideoFeedViewSinglePostDetail {
    this.apply {
        return VideoFeedViewSinglePostDetail(
            postId = postId,
            userId = userId,
            media = getMediaDetails(),
            title = title,
            tags = getHashTags()
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
            profession = profession,
            location = userLastLocationId?.let {
                UserLocationResponse(
                    id = userLastLocationId!!,
                    name = userLastLocationName,
                    lat = userLastLocationLat,
                    lng = userLastLocationLng,
                    zipcode = userLastLocationZipcode,
                )
            },
        )
    }
}
