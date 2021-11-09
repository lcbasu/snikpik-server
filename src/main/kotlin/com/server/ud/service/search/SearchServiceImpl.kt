package com.server.ud.service.search

import com.server.ud.dto.PostsSearchResponse
import com.server.ud.dto.UDSearchRequest
import com.server.ud.provider.search.SearchProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchServiceImpl : SearchService() {

    @Autowired
    private lateinit var searchProvider: SearchProvider

    override fun getPostsForSearchText(UDSearchRequest: UDSearchRequest): PostsSearchResponse? {
        return searchProvider.getPostsForSearchText(UDSearchRequest)
    }

}
