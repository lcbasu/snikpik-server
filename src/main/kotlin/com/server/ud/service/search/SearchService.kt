package com.server.ud.service.search

import com.server.ud.dto.PostsSearchResponse
import com.server.ud.dto.UDSearchRequest

abstract class SearchService {
    abstract fun getPostsForSearchText(UDSearchRequest: UDSearchRequest): PostsSearchResponse?
}
