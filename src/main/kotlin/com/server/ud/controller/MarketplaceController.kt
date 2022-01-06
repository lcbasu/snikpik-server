package com.server.ud.controller

import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.ud.dto.*
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

    @RequestMapping(value = ["/getUsersFeedForType"], method = [RequestMethod.GET])
    fun getUsersFeedForType(@RequestParam zipcode: String,
                                   @RequestParam profileType: ProfileType,
                                   @RequestParam limit: Int,
                                   @RequestParam pagingState: String?): MarketplaceUserFeedV2Response? {
        return marketplaceService.getUsersFeedForType(
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

    @RequestMapping(value = ["/getFeedForMarketplaceUsersV2"], method = [RequestMethod.GET])
    fun getFeedForMarketplaceUsersV2(@RequestParam zipcode: String,
                                     @RequestParam profileCategory: ProfileCategory,
                                     @RequestParam limit: Int,
                                     @RequestParam pagingState: String?): MarketplaceUsersFeedResponseV2 {
        return marketplaceService.getFeedForMarketplaceUsersV2(
            MarketplaceUsersFeedRequestV2(
                zipcode,
                profileCategory,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getFeedForMarketplaceUsersV3"], method = [RequestMethod.GET])
    fun getFeedForMarketplaceUsersV3(@RequestParam zipcode: String,
                                     @RequestParam profileCategory: ProfileCategory,
                                     @RequestParam limit: Int,
                                     @RequestParam pagingState: String?): MarketplaceUsersFeedResponseV3 {
        return marketplaceService.getFeedForMarketplaceUsersV3(
            MarketplaceUsersFeedRequestV2(
                zipcode,
                profileCategory,
                limit,
                pagingState
            )
        )
    }

}
