package com.server.ud.dto

import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.user.UsersByZipcodeAndProfileCategory
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.entities.user.getProfiles

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

data class MarketplaceUserFeedRequest (
    val zipcode: String,
    val profileCategory: ProfileCategory,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)

fun UsersByZipcodeAndProfileCategory.toMarketplaceUserDetail(): MarketplaceUserDetail {
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
