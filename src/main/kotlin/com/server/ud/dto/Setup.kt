package com.server.ud.dto

import com.server.dk.model.MediaDetailsV2
import com.server.ud.enums.UserProfession

// Break down each request and response DTO
// Likes should be a different call and video should be different. And so on.

// Common
//
//data class UserProfession (
//    val professionId: String, // ID1, ID2
//    val professionType: UserProfileType,
//    val name: String // Interior Designer, Architect, etc.
//)

// CommunityFeedView -> CFV

data class CFVCommunityWall(
    val wallId: String,
    val userId: String,
    val media: MediaDetailsV2,
    val title: String?,
    val postedAt: Long,
)

data class CFVUserDetail(
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profession: UserProfession,
)

//data class CFVFollowDetail(
//    override val loggedInUserId: String,
//    override val otherUserId: String,
//    override val followed: Boolean
//): FollowingFollower(loggedInUserId, otherUserId, followed)

// ProfessionalMarketplaceFeed -> PMF

data class PMFUserProfile (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profession: UserProfession, // Like Interior Designer
    val businessName: String?, // at Godrej Interio
    val locationResponse: LocationResponse,
)

data class PMFSingleCard (
    val profession: UserProfession,
    val professionals: List<PMFUserProfile>,
)

data class PMFResult (
    val professionals: List<PMFSingleCard>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

//data class PMFFollowDetail(
//    override val loggedInUserId: String,
//    override val otherUserId: String,
//    override val followed: Boolean
//): FollowingFollower(loggedInUserId, otherUserId, followed)

// SuppliersMarketplaceFeed -> SMF

data class SMFUserProfile (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profession: UserProfession,
    val businessName: String?, // at Oreo Paintings
    val locationResponse: LocationResponse,
)

data class SMFSingleCard (
    val profession: UserProfession,
    val professionals: List<SMFUserProfile>,
)

data class SMFResult (
    val professionals: List<SMFSingleCard>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

//data class SMFFollowDetail(
//    override val loggedInUserId: String,
//    override val otherUserId: String,
//    override val followed: Boolean
//): FollowingFollower(loggedInUserId, otherUserId, followed)

// OthersProfileView -> OPV

data class OPVUserProfile (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profession: UserProfession,
    val locationResponse: LocationResponse,
    val professionalSince: Long?,
)

//data class OPVFollowDetail(
//    override val loggedInUserId: String,
//    override val otherUserId: String,
//    override val followed: Boolean
//): FollowingFollower(loggedInUserId, otherUserId, followed)

data class OPVPostCount(
    val userId: String,
    val posts: Long,
)

data class OPVFollowersCount(
    val userId: String,
    val followers: Long,
)

data class OPVFollowingCount(
    val userId: String,
    val following: Long,
)

data class OPVPostDetail(
    val postId: String,
    val userId: String,
    val media: MediaDetailsV2,
    val title: String?
)

data class OPVPostsList (
    val posts: List<OPVPostDetail>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

data class OPVPostLikesDetail(
    val postId: String,

    val likes: Long,
    val liked: Boolean,
)

// OwnProfileView -> WPV

data class WPVUserProfile (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profession: UserProfession,
    val locationResponse: LocationResponse,
    val professionalSince: Long?,
)

data class WPVPostCount(
    val userId: String,
    val posts: Long,
)

data class WPVFollowersCount(
    val userId: String,
    val followers: Long,
)

data class WPVFollowingCount(
    val userId: String,
    val following: Long,
)

data class WPVPostDetail(
    val postId: String,
    val userId: String,
    val media: MediaDetailsV2,
    val title: String?
)

data class WPVPostLikesDetail(
    val postId: String,

    val likes: Long,
    val liked: Boolean,
)

data class WPVPostsList (
    val posts: List<OPVPostDetail>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

data class WPVSavedSinglePostDetail(
    override val postId: String,
    override val userId: String,
    override val media: MediaDetailsV2?,
    override val title: String?
): PostMiniDetail

data class WPVSavedPostUserDetail(
    val userId: String,
    val name: String,
    val dp: MediaDetailsV2
)

data class WPVSavedPostLikesDetail(
    val postId: String,
    val likes: Long,
    val liked: Boolean
)

data class WPVSavedPostsList (
    val posts: List<WPVSavedSinglePostDetail>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

