package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.Post
import com.server.ud.enums.LikeUpdateAction
import com.server.ud.enums.ResourceType
import java.time.Instant

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
)


fun Like.toSavedLikeResponse(): SavedLikeResponse {
    this.apply {
        return SavedLikeResponse(
            likeId = likeId,
            createdAt = DateUtils.getEpoch(createdAt),
            resourceId = resourceId,
            resourceType = resourceType,
            userId = userId,
            liked = liked,
        )
    }
}

