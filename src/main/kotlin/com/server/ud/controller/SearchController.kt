package com.server.ud.controller

import com.server.ud.dto.PostsSearchResponse
import com.server.ud.dto.UDSearchRequest
import com.server.ud.service.search.SearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("ud/post")
class SearchController {

    @Autowired
    private lateinit var searchService: SearchService

    @RequestMapping(value = ["/getPostsForSearchText"], method = [RequestMethod.GET])
    fun getPostsForSearchText(@RequestParam typedText: String, @RequestParam from: Int, @RequestParam size: Int): PostsSearchResponse? {
        return searchService.getPostsForSearchText(UDSearchRequest(
            typedText = typedText,
            from = from,
            size = size
        ))
    }
}
