package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.service.social.HashTagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/hashTag")
class HashTagController {

    @Autowired
    private lateinit var hashTagService: HashTagService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveHashTags(@RequestBody request: SaveHashTagsRequest): SavedHashTagsResponse? {
        return hashTagService.saveHashTags(request)
    }
}
