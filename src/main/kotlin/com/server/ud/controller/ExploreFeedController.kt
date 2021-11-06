package com.server.ud.controller

import com.server.ud.dto.ExploreFeedRequest
import com.server.ud.dto.ExploreTabViewResponse
import com.server.ud.dto.ExploreTabViewUserDetail
import com.server.ud.enums.CategoryV2
import com.server.ud.service.post.ExploreFeedService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("ud/feed/explore")
class ExploreFeedController {

    @Autowired
    private lateinit var exploreFeedService: ExploreFeedService

    @RequestMapping(value = ["/getFeedForCategory"], method = [RequestMethod.GET])
    fun getFeedForCategory(@RequestParam category: CategoryV2,
                           @RequestParam forDate: String,
                           @RequestParam limit: Int,
                           @RequestParam pagingState: String? = null): ExploreTabViewResponse {
        return exploreFeedService.getFeedForCategory(
            ExploreFeedRequest(
                category,
                forDate,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getUserInfo"], method = [RequestMethod.GET])
    fun getUserInfo(@RequestParam userId: String): ExploreTabViewUserDetail {
        return exploreFeedService.getUserInfo(userId)
    }
}
