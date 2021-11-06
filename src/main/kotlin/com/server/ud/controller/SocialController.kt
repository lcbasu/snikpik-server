package com.server.ud.controller

import com.server.ud.dto.SocialRelationRequest
import com.server.ud.dto.SocialRelationResponse
import com.server.ud.service.social.SocialService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
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
}
