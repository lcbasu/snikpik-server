package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.DateUtils
import com.server.ud.entities.like.Like
import com.server.ud.enums.LikeUpdateAction
import com.server.ud.enums.ResourceType
import com.server.ud.provider.like.LikesCountByResourceProvider

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
