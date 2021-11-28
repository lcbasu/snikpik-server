package com.server.ud.service.marketplace

import com.server.ud.dto.MarketplaceProfileTypesFeedRequest
import com.server.ud.dto.MarketplaceProfileTypesFeedResponse
import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.dto.MarketplaceUserFeedResponse

abstract class MarketplaceService {
    abstract fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): MarketplaceUserFeedResponse?
    abstract fun getFeedForMarketplaceProfileTypes(request: MarketplaceProfileTypesFeedRequest): MarketplaceProfileTypesFeedResponse
}
