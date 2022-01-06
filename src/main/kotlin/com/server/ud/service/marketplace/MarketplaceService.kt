package com.server.ud.service.marketplace

import com.server.ud.dto.*

abstract class MarketplaceService {
    abstract fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): MarketplaceUserFeedResponse?
    abstract fun getFeedForMarketplaceProfileTypes(request: MarketplaceProfileTypesFeedRequest): MarketplaceProfileTypesFeedResponse
    abstract fun getFeedForMarketplaceUsersV2(request: MarketplaceUsersFeedRequestV2): MarketplaceUsersFeedResponseV2
    abstract fun getFeedForMarketplaceUsersV3(request: MarketplaceUsersFeedRequestV2): MarketplaceUsersFeedResponseV3
    abstract fun getUsersFeedForType(request: MarketplaceUserFeedRequest): MarketplaceUserFeedV2Response?
}
