package com.server.ud.dto

import com.server.dk.model.MediaDetailsV2
import com.server.ud.enums.CategoryV2
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

interface Location {
    val id: String // ID at the city or 5x5 KM square block level (Need to figure out a way)
    val name: String // City Level grouping and naming
    val lat: Double
    val lng:Double
}

data class UserLocation(
    override val id: String,
    override val name: String,
    override val lat: Double,
    override val lng: Double,
): Location


data class PostLocation(
    override val id: String,
    override val name: String,
    override val lat: Double,
    override val lng: Double,
): Location

//data class CategoryV2 (
//    val categoryId: String,
//    val name: String
//)

data class HashTagData (
    val tagId: String,
    val displayName: String
)

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
    val media: MediaDetailsV2
    val title: String?
}

// ExploreTabView -> ETV
data class ETVCategories(
    val categories: List<CategoryV2>
)

data class ETVPostDetail(
    override val postId: String,
    override val userId: String,
    override val media: MediaDetailsV2,
    override val title: String?
): PostMiniDetail

data class ETVUserDetail(
    val userId: String,
    val name: String,
    val dp: MediaDetailsV2
)

data class ETVLikesDetail(
    val postId: String,
    val likes: Long,
    val liked: Boolean
)

data class ETVResultList(
    val posts: List<ETVPostDetail>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails



// VideoFeedView -> VFV

data class VFVSinglePostDetail(
    override val postId: String,
    override val userId: String,
    override val media: MediaDetailsV2,
    override val title: String?
): PostMiniDetail

data class VFVSingleTagDetail(
    val tags: List<HashTagData>
)

data class VFVSingleUserDetail(
    val userId: String,
    val handle: String,
    val verified: Boolean,
    val dp: MediaDetailsV2,
    val profession: UserProfession,
    val location: UserLocation,
)

data class VFVFollowDetail(
    override val loggedInUserId: String,
    override val otherUserId: String,
    override val followed: Boolean
): FollowingFollower

data class VFVSingleLikesDetail(
    val postId: String,

    val likes: Long,
    val liked: Boolean,
)

data class VFVSingleCommentsDetail(
    val postId: String,

    val comments: Long,
    val commented: Boolean,
)

data class VFVSingleSaveDetail(
    val postId: String,

    val saves: Long,
    val saved: Boolean,
)

data class VFVResultList(
    val posts: List<VFVSinglePostDetail>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

// PostCommentView -> PCV
data class PCVPostDetail (
    val postId: String,
    val media: MediaDetailsV2,
)

data class PCVCommentCountDetail (
    val postId: String,
    val count: Long,
)

data class PCVSingleCommentDetail (
    val commentId: String,
    val userId: String,
    val text: String,
    val commentedAt: Long,
)

data class PCVResultList(
    val comments: List<PCVSingleCommentDetail>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

data class PCVSingleCommentUserDetail (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
)

data class PCVSingleCommentLikeDetail (
    val commentId: String,
    val likes: Long,
    val liked: Boolean,
)

data class PCVSingleCommentReplyDetail (
    val commentId: String,
    val replyId: String,
    val text: String,
    val repliedAt: Long,
)

data class PCVSingleCommentReplyUserDetail (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
)

data class PCVSingleCommentRepliesList(
    val replies: List<PCVSingleCommentReplyDetail>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

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

data class CFVFollowDetail(
    override val loggedInUserId: String,
    override val otherUserId: String,
    override val followed: Boolean
): FollowingFollower

// ProfessionalMarketplaceFeed -> PMF

data class PMFUserProfile (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profession: UserProfession, // Like Interior Designer
    val businessName: String?, // at Godrej Interio
    val location: Location,
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

data class PMFFollowDetail(
    override val loggedInUserId: String,
    override val otherUserId: String,
    override val followed: Boolean
): FollowingFollower

// SuppliersMarketplaceFeed -> SMF

data class SMFUserProfile (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profession: UserProfession,
    val businessName: String?, // at Oreo Paintings
    val location: Location,
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

data class SMFFollowDetail(
    override val loggedInUserId: String,
    override val otherUserId: String,
    override val followed: Boolean
): FollowingFollower

// OthersProfileView -> OPV

data class OPVUserProfile (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profession: UserProfession,
    val location: Location,
    val professionalSince: Long?,
)

data class OPVFollowDetail(
    override val loggedInUserId: String,
    override val otherUserId: String,
    override val followed: Boolean
): FollowingFollower

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
    val location: Location,
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
    override val media: MediaDetailsV2,
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

