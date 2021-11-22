package com.server.ud.dto

import com.server.common.dto.ProfileTypeResponse
import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.user.UsersByZipcodeAndProfileType
import com.server.ud.entities.user.getMediaDetailsForDP

data class MarketplaceUserDetail(
    val userId: String,
    val name: String?,
    val verified: Boolean,
    val dp: MediaDetailsV2?,
    val profileTypeToShow: ProfileType,
    val locationName: String?,
)

data class MarketplaceUserFeedResponse(
    val users: List<MarketplaceUserDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class MarketplaceProfileTypesFeedResponse(
    val profileTypes: List<ProfileTypeResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class MarketplaceUserFeedRequest (
    val zipcode: String,
    val profileType: ProfileType,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)

data class MarketplaceProfileTypesFeedRequest (
    val zipcode: String,
    val profileCategory: ProfileCategory,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)

fun UsersByZipcodeAndProfileType.toMarketplaceUserDetail(): MarketplaceUserDetail {
    this.apply {
        return MarketplaceUserDetail(
            userId = userId,
            name = fullName,
            dp = getMediaDetailsForDP(),
            // Here profile will always be present
            profileTypeToShow = profileType,
            locationName = userLocationName,
            verified = verified
        )
    }
}
