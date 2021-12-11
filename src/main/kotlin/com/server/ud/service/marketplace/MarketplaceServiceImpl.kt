package com.server.ud.service.marketplace

import com.server.common.dto.toProfileTypeResponse
import com.server.ud.dto.*
import com.server.ud.provider.user.ProfileTypesByZipcodeAndProfileCategoryProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.provider.user.UsersByNearbyZipcodeAndProfileTypeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MarketplaceServiceImpl : MarketplaceService() {

    @Autowired
    private lateinit var usersByNearbyZipcodeAndProfileTypeProvider: UsersByNearbyZipcodeAndProfileTypeProvider

    @Autowired
    private lateinit var profileTypesByZipcodeAndProfileCategoryProvider: ProfileTypesByZipcodeAndProfileCategoryProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    override fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): MarketplaceUserFeedResponse? {
        val result = usersByNearbyZipcodeAndProfileTypeProvider.getFeedForMarketplaceUsers(request)
        return MarketplaceUserFeedResponse(
            users = result.content?.filterNotNull()?.mapNotNull { it.toMarketplaceUserDetail(userV2Provider) } ?: emptyList(),
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
