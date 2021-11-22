package com.server.ud.service.marketplace

import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.dto.MarketplaceUserFeedResponse
import com.server.ud.dto.toMarketplaceUserDetail
import com.server.ud.provider.user.UsersByZipcodeAndProfileProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MarketplaceServiceImpl : MarketplaceService() {

    @Autowired
    private lateinit var usersByZipcodeAndProfileProvider: UsersByZipcodeAndProfileProvider

    override fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): MarketplaceUserFeedResponse {
        val result = usersByZipcodeAndProfileProvider.getFeedForMarketplaceUsers(request)
        return MarketplaceUserFeedResponse(
            users = result.content?.filterNotNull()?.map { it.toMarketplaceUserDetail() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }
}
