package com.server.ud.dto

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
    override val media: MediaDetailsV2?,
    override val title: String?
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
    val forDate: String,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)

fun PostsByCategory.toExploreTabViewPostDetail(): ExploreTabViewPostDetail {
    this.apply {
        return ExploreTabViewPostDetail(
            postId = postId,
            userId = userId,
            media = getMediaDetails(),
            title = title,
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