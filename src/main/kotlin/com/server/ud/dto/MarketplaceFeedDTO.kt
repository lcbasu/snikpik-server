package com.server.ud.dto

import com.server.common.dto.*
import com.server.common.dto.PaginationRequest
import com.server.common.dto.PaginationResponse
import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.common.model.MediaDetailsV2
import com.server.ud.entities.user.UsersByNearbyZipcodeAndProfileType
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.entities.user.toUserV2PublicMiniDataResponse
import com.server.ud.provider.user.UserV2Provider

data class MarketplaceUserDetail(
    val userId: String,
    val name: String?,
    val verified: Boolean,
    val dp: MediaDetailsV2?,
    val profileTypeToShow: ProfileTypeResponse,
    val locationName: String?,
)

data class MarketplaceUserFeedResponse(
    val users: List<MarketplaceUserDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class MarketplaceUserFeedV2Response(
    val users: List<UserV2PublicMiniDataResponse>,
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

data class MarketplaceUsersFeedResponseV2(
    val users: List<ProfileTypeWithUsersResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class MarketplaceUsersFeedResponseV3(
    val users: List<ProfileTypeWithUsersResponseV3>,
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

data class MarketplaceUsersFeedRequestV2 (
    val zipcode: String,
    val profileCategory: ProfileCategory,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)

fun UsersByNearbyZipcodeAndProfileType.toUserV2PublicMiniDataResponse(userV2Provider: UserV2Provider): UserV2PublicMiniDataResponse? {
    this.apply {
        try {
            val user = userV2Provider.getUser(userId) ?: error("User not found for userId: $userId")
            return user.toUserV2PublicMiniDataResponse()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}


fun UsersByNearbyZipcodeAndProfileType.toMarketplaceUserDetail(userV2Provider: UserV2Provider): MarketplaceUserDetail? {
    this.apply {
        try {
            val user = userV2Provider.getUser(userId) ?: error("User not found for userId: $userId")
            return MarketplaceUserDetail(
                userId = userId,
                name = user.fullName,
                dp = user.getMediaDetailsForDP(),
                // Here profile will always be present
                profileTypeToShow = profileType.toProfileTypeResponse(),

                // Location of the work
                locationName = user.permanentLocationName,
                verified = user.verified
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
