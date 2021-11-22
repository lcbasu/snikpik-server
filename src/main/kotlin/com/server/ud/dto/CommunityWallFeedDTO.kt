package com.server.ud.dto

import com.server.common.dto.ProfileTypeResponse
import com.server.common.dto.toProfileTypeResponse
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.post.NearbyPostsByZipcode
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.entities.user.getProfiles

data class CommunityWallViewPostDetail(
    val description: String?,
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?
): PostMiniDetail

data class CommunityWallViewUserDetail(
    val userId: String,
    val handle: String?,
    val name: String?,
    val dp: MediaDetailsV2?,
    val verified: Boolean?,
    val profileTypeToShow: ProfileTypeResponse?,
)

data class CommunityWallViewResponse(
    val posts: List<CommunityWallViewPostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)


data class CommunityWallFeedRequest (
    val zipcode: String,
    val forDate: String, // YYYY-MM-DD
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)


fun NearbyPostsByZipcode.toCommunityWallViewPostDetail(): CommunityWallViewPostDetail {
    this.apply {
        return CommunityWallViewPostDetail(
            postId = postId,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            media = getMediaDetails(),
            title = title,
            description = description
        )
    }
}

fun UserV2.toCommunityWallViewUserDetail(): CommunityWallViewUserDetail {
    this.apply {
        return CommunityWallViewUserDetail(
            userId = userId,
            handle = handle,
            name = fullName,
            verified = verified,
            dp = getMediaDetailsForDP(),
            profileTypeToShow = getProfiles().firstOrNull()?.toProfileTypeResponse(),
        )
    }
}
