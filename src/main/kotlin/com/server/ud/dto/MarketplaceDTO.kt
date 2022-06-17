package com.server.ud.dto

import com.server.common.dto.PaginationRequest
import com.server.common.dto.PaginationResponse
import com.server.common.model.MediaDetailsV2
import com.server.common.enums.ProfileType

// ProfessionalMarketplaceFeed -> PMF

data class PMFUserProfile (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
    val profileType: ProfileType, // Like Interior Designer
    val businessName: String?, // at Godrej Interio
    val locationResponse: LocationResponse,
)

data class PMFSingleCard (
    val profileType: ProfileType,
    val professionals: List<PMFUserProfile>,
)

data class ProfessionalsFeedResponse(
    val professionals: List<PMFSingleCard>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)


data class ProfessionalsFeedRequest (
    val profileType: ProfileType,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)
