package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.ProfileTypeResponse
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProfileType
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.post.BookmarkedPostsByUser
import com.server.ud.entities.post.LikedPostsByUser
import com.server.ud.entities.post.PostsByUser
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.entities.user.getProfiles

data class LikedPostsByUserPostDetail(
    val likedByUserId: String,
    val likedAt: Long,
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

data class LikedPostsByUserResponse(
    val posts: List<LikedPostsByUserPostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class LikedPostsByUserRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class BookmarkedPostsByUserPostDetail(
    val bookmarkedByUserId: String,
    val bookmarkedAt: Long,
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

data class BookmarkedPostsByUserResponse(
    val posts: List<BookmarkedPostsByUserPostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class BookmarkedPostsByUserRequest (
    val userId: String,
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

data class PostsByUserRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2HandleRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newHandle: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2DPRequest (
    // Take it from request for any update related action
//    val userId: String,
    val dp: MediaDetailsV2,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2ProfilesRequest (
    // Take it from request for any update related action
//    val userId: String,
    val profiles: Set<ProfileType>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2NameRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2LocationRequest (
    // Take it from request for any update related action
//    val userId: String,
    val lat: Double,
    val lng: Double,
    var zipcode: String,
    val name: String?,
    var googlePlaceId: String?,
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class AWSLambdaAuthResponse(
    var userId: String,
    val anonymous: Boolean = false
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfilePageUserDetailsResponse(
    var userId: String,
    val fullName: String?,
    val uid: String?,
    var createdAt: Long?,
    var handle: String?,
    var dp: MediaDetailsV2?, // MediaDetailsV2
    var verified: Boolean?,
    var profileToShow: ProfileTypeResponse?,
    val userLastLocationName: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserV2Response(
    var userId: String,
    val fullName: String?,
    val uid: String?,
    val anonymous: Boolean?,
    val absoluteMobile: String?,
    val countryCode: String?,
    val notificationToken: String?,
    val notificationTokenProvider: NotificationTokenProvider?,
    var createdAt: Long?,
    var handle: String?,
    var dp: MediaDetailsV2?, // MediaDetailsV2
    var verified: Boolean?,
    var profiles: AllProfileTypeResponse?,
    var userLastLocationZipcode: String?,
    var userLastGooglePlaceId: String?,
    var userLastLocationId: String?,
    val userLastLocationName: String?,
    val userLastLocationLat: Double?,
    val userLastLocationLng: Double?,
)

fun UserV2.toSavedUserV2Response(): SavedUserV2Response {
    this.apply {
        return SavedUserV2Response(
            userId = userId,
            fullName = fullName,
            uid = uid,
            anonymous = anonymous,
            absoluteMobile = absoluteMobile,
            countryCode = countryCode,
            notificationToken = notificationToken,
            notificationTokenProvider = notificationTokenProvider,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            dp = getMediaDetailsForDP(),
            verified = verified,
            profiles = getProfiles(),
            userLastLocationZipcode = userLastLocationZipcode,
            userLastGooglePlaceId = userLastGooglePlaceId,
            userLastLocationId = userLastLocationId,
            userLastLocationName = userLastLocationName,
            userLastLocationLat = userLastLocationLat,
            userLastLocationLng = userLastLocationLng,
        )
    }
}

fun UserV2.toProfilePageUserDetailsResponse(): ProfilePageUserDetailsResponse {
    this.apply {
        return ProfilePageUserDetailsResponse(
            userId = userId,
            fullName = fullName,
            uid = uid,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            dp = getMediaDetailsForDP(),
            verified = verified,
            profileToShow = getProfiles().profileTypes.firstOrNull(),
            userLastLocationName = userLastLocationName,
        )
    }
}

fun LikedPostsByUser.toLikedPostsByUserPostDetail(): LikedPostsByUserPostDetail {
    this.apply {
        return LikedPostsByUserPostDetail(
            postId = postId,
            userId = postedByUserId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(postCreatedAt),
            likedAt = DateUtils.getEpoch(createdAt),
            likedByUserId = userId,
            description = description,
        )
    }
}

fun BookmarkedPostsByUser.toBookmarkedPostsByUserPostDetail(): BookmarkedPostsByUserPostDetail {
    this.apply {
        return BookmarkedPostsByUserPostDetail(
            postId = postId,
            userId = postedByUserId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(postCreatedAt),
            bookmarkedAt = DateUtils.getEpoch(createdAt),
            bookmarkedByUserId = userId,
            description = description,
        )
    }
}


fun PostsByUser.toPostsByUserPostDetail(): PostsByUserPostDetail {
    this.apply {
        return PostsByUserPostDetail(
            postId = postId,
            userId = userId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(createdAt),
            description = description,
        )
    }
}
