package com.server.ud.controller

import com.server.ud.dto.NearbyFeedRequest
import com.server.ud.dto.VideoFeedViewResultList
import com.server.ud.dto.VideoFeedViewSingleUserDetail
import com.server.ud.service.post.NearbyFeedService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("ud/feed/nearby")
class NearbyFeedController {

    @Autowired
    private lateinit var nearbyFeedService: NearbyFeedService

    @RequestMapping(value = ["/getNearbyFeed"], method = [RequestMethod.GET])
    fun getNearbyFeed(@RequestParam zipcode: String,
                      @RequestParam forDate: String,
                      @RequestParam limit: Int,
                      @RequestParam pagingState: String? = null): VideoFeedViewResultList {
        return nearbyFeedService.getNearbyFeed(
            NearbyFeedRequest(
                zipcode,
                forDate,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getUserInfo"], method = [RequestMethod.GET])
    fun getUserInfo(@RequestParam userId: String): VideoFeedViewSingleUserDetail {
        return nearbyFeedService.getUserInfo(userId)
    }

//
//    @RequestMapping(value = ["/getUserInfo"], method = [RequestMethod.GET])
//    fun getUserInfo(@RequestParam userId: String): ExploreTabViewUserDetail {
//        return exploreFeedService.getUserInfo(userId)
//    }
//
//    @RequestMapping(value = ["/getPostLikeInfo"], method = [RequestMethod.GET])
//    fun getPostLikeInfo(@RequestParam postId: String): ResourceLikesDetail {
//        return exploreFeedService.getPostLikeInfo(postId)
//    }
}
