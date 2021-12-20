package com.server.ud.service.marketplace

import com.server.common.dto.toProfileTypeResponse
import com.server.ud.dto.*
import com.server.ud.provider.marketplace.MarketplaceProvider
import com.server.ud.provider.user.ProfileTypesByNearbyZipcodeProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.provider.user.UsersByNearbyZipcodeAndProfileTypeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MarketplaceServiceImpl : MarketplaceService() {

    @Autowired
    private lateinit var usersByNearbyZipcodeAndProfileTypeProvider: UsersByNearbyZipcodeAndProfileTypeProvider

    @Autowired
    private lateinit var profileTypesByNearbyZipcodeProvider: ProfileTypesByNearbyZipcodeProvider

    @Autowired
    private lateinit var marketplaceProvider: MarketplaceProvider

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
        val result = profileTypesByNearbyZipcodeProvider.getFeedForMarketplaceProfileTypes(request)
        return MarketplaceProfileTypesFeedResponse(
            profileTypes = result.content?.filterNotNull()?.map { it.profileType.toProfileTypeResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getFeedForMarketplaceUsersV2(request: MarketplaceUsersFeedRequestV2): MarketplaceUsersFeedResponseV2 {
        return marketplaceProvider.getFeedForMarketplaceUsersV2(request);
    }
}
