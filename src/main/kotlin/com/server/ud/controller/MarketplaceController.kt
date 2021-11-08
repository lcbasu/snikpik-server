package com.server.ud.controller

import com.server.common.enums.ProfileCategory
import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.dto.MarketplaceUserFeedResponse
import com.server.ud.service.marketplace.MarketplaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("ud/feed/marketplace")
class MarketplaceController {

    @Autowired
    private lateinit var marketplaceService: MarketplaceService

    @RequestMapping(value = ["/getFeedForMarketplaceUsers"], method = [RequestMethod.GET])
    fun getFeedForMarketplaceUsers(@RequestParam profileCategory: ProfileCategory,
                            @RequestParam limit: Int,
                            @RequestParam pagingState: String?): MarketplaceUserFeedResponse {
        return marketplaceService.getFeedForMarketplaceUsers(
            MarketplaceUserFeedRequest(
                profileCategory,
                limit,
                pagingState
            )
        )
    }

}
