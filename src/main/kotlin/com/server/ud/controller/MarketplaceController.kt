package com.server.ud.controller

import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.ud.dto.MarketplaceProfileTypesFeedRequest
import com.server.ud.dto.MarketplaceProfileTypesFeedResponse
import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.dto.MarketplaceUserFeedResponse
import com.server.ud.service.marketplace.MarketplaceService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("ud/feed/marketplace")
class MarketplaceController {

    @Autowired
    private lateinit var marketplaceService: MarketplaceService

    @RequestMapping(value = ["/getFeedForMarketplaceUsers"], method = [RequestMethod.GET])
    fun getFeedForMarketplaceUsers(@RequestParam zipcode: String,
                                   @RequestParam profileType: ProfileType,
                                   @RequestParam limit: Int,
                                   @RequestParam pagingState: String?): MarketplaceUserFeedResponse? {
        return marketplaceService.getFeedForMarketplaceUsers(
            MarketplaceUserFeedRequest(
                zipcode,
                profileType,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getFeedForMarketplaceProfileTypes"], method = [RequestMethod.GET])
    fun getFeedForMarketplaceProfileTypes(@RequestParam zipcode: String,
                                   @RequestParam profileCategory: ProfileCategory,
                                   @RequestParam limit: Int,
                                   @RequestParam pagingState: String?): MarketplaceProfileTypesFeedResponse {
        return marketplaceService.getFeedForMarketplaceProfileTypes(
            MarketplaceProfileTypesFeedRequest(
                zipcode,
                profileCategory,
                limit,
                pagingState
            )
        )
    }

}
