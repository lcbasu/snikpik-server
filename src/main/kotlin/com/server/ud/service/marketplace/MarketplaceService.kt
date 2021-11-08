package com.server.ud.service.marketplace

import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.dto.MarketplaceUserFeedResponse

abstract class MarketplaceService {
    abstract fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): MarketplaceUserFeedResponse
}
