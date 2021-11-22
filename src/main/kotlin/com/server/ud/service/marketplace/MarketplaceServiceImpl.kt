package com.server.ud.service.marketplace

import com.server.common.dto.toProfileTypeResponse
import com.server.ud.dto.*
import com.server.ud.provider.user.ProfileTypesByZipcodeAndProfileCategoryProvider
import com.server.ud.provider.user.UsersByZipcodeAndProfileTypeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MarketplaceServiceImpl : MarketplaceService() {

    @Autowired
    private lateinit var usersByZipcodeAndProfileTypeProvider: UsersByZipcodeAndProfileTypeProvider

    @Autowired
    private lateinit var profileTypesByZipcodeAndProfileCategoryProvider: ProfileTypesByZipcodeAndProfileCategoryProvider

    override fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): MarketplaceUserFeedResponse {
        val result = usersByZipcodeAndProfileTypeProvider.getFeedForMarketplaceUsers(request)
        return MarketplaceUserFeedResponse(
            users = result.content?.filterNotNull()?.map { it.toMarketplaceUserDetail() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getFeedForMarketplaceProfileTypes(request: MarketplaceProfileTypesFeedRequest): MarketplaceProfileTypesFeedResponse {
        val result = profileTypesByZipcodeAndProfileCategoryProvider.getFeedForMarketplaceProfileTypes(request)
        return MarketplaceProfileTypesFeedResponse(
            profileTypes = result.content?.filterNotNull()?.map { it.profileType.toProfileTypeResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }
}
