package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.like.Like
import com.server.ud.enums.LikeUpdateAction
import com.server.ud.enums.ResourceType
import com.server.ud.provider.like.LikesCountByResourceProvider

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

data class ResourceLikesReportDetailForUser(
    val userId: String,
    val liked: Boolean
)

data class ResourceLikesReportDetail(
    val resourceId: String,
    val likes: Long,
    val userLevelInfo: ResourceLikesReportDetailForUser
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveLikeRequest(
    var resourceType: ResourceType,
    var resourceId: String,
    var action: LikeUpdateAction
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedLikeResponse(
    var likeId: String,
    var createdAt: Long,
    var resourceId: String,
    var resourceType: ResourceType,
    var userId: String,
    var liked: Boolean,
    val totalLikes: Long,
)

fun Like.toSavedLikeResponse(provider: LikesCountByResourceProvider): SavedLikeResponse {
    this.apply {
        return SavedLikeResponse(
            likeId = likeId,
            createdAt = DateUtils.getEpoch(createdAt),
            resourceId = resourceId,
            resourceType = resourceType,
            userId = userId,
            liked = liked,
            totalLikes = provider.getLikesCountByResource(resourceId)?.likesCount ?: 0
        )
    }
}
