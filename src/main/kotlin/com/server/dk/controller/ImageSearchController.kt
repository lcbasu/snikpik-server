package com.server.dk.controller

import com.server.dk.dto.ThirdPartyImageSearchResponse
import com.server.dk.service.ImageSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("images")
class ImageSearchController {
    @Autowired
    private lateinit var imageSearchService: ImageSearchService

    // This call will be made only by a logged in user
    @RequestMapping(value = ["/search"], method = [RequestMethod.GET])
    fun getImagesForQuery(
        @RequestParam(value = "query", required = true) query: String
    ): ThirdPartyImageSearchResponse? {
        return imageSearchService.getImagesForQuery(query)
    }

}
