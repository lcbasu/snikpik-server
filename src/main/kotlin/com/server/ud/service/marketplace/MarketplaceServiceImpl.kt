package com.server.ud.service.marketplace

import com.server.common.provider.AuthProvider
import com.server.ud.dto.*
import com.server.ud.provider.user.UsersByZipcodeAndProfileProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MarketplaceServiceImpl : MarketplaceService() {

    @Autowired
    private lateinit var usersByZipcodeAndProfileProvider: UsersByZipcodeAndProfileProvider

    @Autowired
    private lateinit var authProvider: AuthProvider

    override fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): MarketplaceUserFeedResponse {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.userV2
        val result = usersByZipcodeAndProfileProvider.getFeedForMarketplaceUsers(user, request)
        return MarketplaceUserFeedResponse(
            users = result.content?.filterNotNull()?.map { it.toMarketplaceUserDetail(user.userLastLocationName) } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }
}
