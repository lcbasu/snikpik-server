package com.server.ud.service.marketplace

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.dto.MarketplaceUserFeedResponse
import com.server.ud.dto.toMarketplaceUserDetail
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.provider.user.UsersByZipcodeAndProfileProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MarketplaceServiceImpl : MarketplaceService() {

    @Autowired
    private lateinit var usersByZipcodeAndProfileProvider: UsersByZipcodeAndProfileProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    override fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): MarketplaceUserFeedResponse {
        val userDetailsFromToken = securityProvider.validateRequest()
        val user = userV2Provider.getUser(userId = userDetailsFromToken.getUserIdToUse()) ?: error("Unable to find user for Id: ${userDetailsFromToken.getUserIdToUse()}")
        val result = usersByZipcodeAndProfileProvider.getFeedForMarketplaceUsers(user, request)
        return MarketplaceUserFeedResponse(
            users = result.content?.filterNotNull()?.map { it.toMarketplaceUserDetail(user.userLastLocationName) } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }
}
