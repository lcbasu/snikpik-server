package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.service.post.CommunityWallFeedService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("ud/feed/communityWall")
class CommunityWallFeedController {

    @Autowired
    private lateinit var communityWallFeedService: CommunityWallFeedService

    @RequestMapping(value = ["/getFeed"], method = [RequestMethod.GET])
    fun getFeed(@RequestParam zipcode: String,
                @RequestParam limit: Int,
                @RequestParam pagingState: String? = null): CommunityWallViewResponse {
        return communityWallFeedService.getFeed(
            CommunityWallFeedRequest(
                zipcode,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getUserInfo"], method = [RequestMethod.GET])
    fun getUserInfo(@RequestParam userId: String): CommunityWallViewUserDetail {
        return communityWallFeedService.getUserInfo(userId)
    }
}
