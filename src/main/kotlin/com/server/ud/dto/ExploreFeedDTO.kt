package com.server.ud.dto

import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.enums.CategoryV2

// ExploreTabView -> ETV
data class ExploreTabViewCategories(
    val categories: List<CategoryV2>
)

data class ExploreTabViewPostDetail(
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

data class ExploreTabViewUserDetail(
    val userId: String,
    val name: String?,
    val dp: MediaDetailsV2?
)

data class ExploreTabViewResponse(
    val posts: List<ExploreTabViewPostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)


data class ExploreFeedRequest (
    val category: CategoryV2,
    val forDate: String,// YYYY-MM-DD
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

fun PostsByCategory.toExploreTabViewPostDetail(): ExploreTabViewPostDetail {
    this.apply {
        return ExploreTabViewPostDetail(
            postId = postId,
            userId = userId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(createdAt),
            description = description
        )
    }
}

fun UserV2.toExploreTabViewUserDetail(): ExploreTabViewUserDetail {
    this.apply {
        return ExploreTabViewUserDetail(
            userId = userId,
            name = fullName,
            dp = getMediaDetailsForDP(),
        )
    }
}
