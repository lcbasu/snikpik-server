package com.server.ud.dto

import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.entities.post.getCategories
import com.server.ud.entities.post.getHashTags
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.enums.CategoryV2

// ExploreTabView -> ETV
data class ExploreTabViewCategories(
    val categories: List<CategoryV2>
)

//data class ExploreTabViewPostDetail(
//    override val postId: String,
//    override val userId: String,
//    override val createdAt: Long,
//    override val media: MediaDetailsV2?,
//    override val title: String?,
//    override val description: String?
//): PostCommonDetail

data class ExploreTabViewUserDetail(
    val userId: String,
    val name: String?,
    val dp: MediaDetailsV2?
)

data class ExploreTabViewResponse(
    val posts: List<SavedPostResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)


data class ExploreFeedRequest (
    val category: CategoryV2,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

fun PostsByCategory.toSavedPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            media = getMediaDetails(),
            title = title,
            description = description,
            postType = postType,
            tags = getHashTags(),
            locationId = locationId,
            zipcode = zipcode,
            locationName = locationName,
            city = city,
            categories = getCategories(),


            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
            mediaDetails = getMediaDetails(),
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
