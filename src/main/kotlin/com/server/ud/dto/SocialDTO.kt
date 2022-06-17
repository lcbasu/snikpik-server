package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.dto.PaginationRequest
import com.server.common.dto.PaginationResponse
import com.server.common.utils.DateUtils
import com.server.ud.entities.social.FollowersByUser
import com.server.ud.entities.social.SocialRelation

data class FollowersByUserResponse (
    var userId: String,
    var createdAt: Long,
    var followerUserId: String,
    val userHandle: String? = null,
    var followerHandle: String? = null,
    val userFullName: String? = null,
    val followerFullName: String? = null,
)

data class FollowersResponse(
    var userId: String,
    val followers: List<FollowersByUserResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)


data class GetFollowersRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SocialRelationRequest (
    var toUserId: String,
    var following: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SocialRelationResponse (
    var fromUserId: String,
    var toUserId: String,
    val following: Boolean,
)

fun SocialRelation.toSocialRelationResponse(): SocialRelationResponse {
    this.apply {
        return SocialRelationResponse(
            fromUserId = fromUserId,
            toUserId = toUserId,
            following = following,
        )
    }
}


fun FollowersByUser.toSocialRelationResponse(): FollowersByUserResponse {
    this.apply {
        return FollowersByUserResponse(
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            followerUserId = followerUserId,
            userHandle = userHandle,
            followerHandle = followerHandle,
            userFullName = userFullName,
            followerFullName = followerFullName,
        )
    }
}


