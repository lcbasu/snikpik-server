package com.server.ud.controller

import com.server.ud.dto.FollowersResponse
import com.server.ud.dto.GetFollowersRequest
import com.server.ud.dto.SocialRelationRequest
import com.server.ud.dto.SocialRelationResponse
import com.server.ud.service.social.SocialService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/social")
class SocialController {

    @Autowired
    private lateinit var socialService: SocialService

    @RequestMapping(value = ["/getRelation"], method = [RequestMethod.GET])
    fun getRelation(@RequestParam otherUserId: String): SocialRelationResponse? {
        return socialService.getRelation(otherUserId)
    }

    @RequestMapping(value = ["/setRelation"], method = [RequestMethod.POST])
    fun setRelation(@RequestBody request: SocialRelationRequest): SocialRelationResponse {
        return socialService.setRelation(request)
    }

    @RequestMapping(value = ["/getFollowers"], method = [RequestMethod.GET])
    fun getFollowers(@RequestParam userId: String,
                     @RequestParam limit: Int,
                     @RequestParam pagingState: String? = null): FollowersResponse? {
        return socialService.getFollowers(GetFollowersRequest(
            userId = userId,
            limit = limit,
            pagingState = pagingState,
        ))
    }
}
