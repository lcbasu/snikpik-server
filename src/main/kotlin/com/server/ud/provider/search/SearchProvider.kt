package com.server.ud.provider.search

import com.server.ud.dto.PostsSearchResponse
import com.server.ud.dto.UDSearchRequest
import com.server.ud.dto.getESPost
import com.server.ud.dto.getPost
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.MultiMatchQueryBuilder
import org.elasticsearch.index.query.Operator
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class SearchProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var restHighLevelClient: RestHighLevelClient

    fun getPostsForSearchText(request: UDSearchRequest): PostsSearchResponse? {
        return try {
            val searchRequest = SearchRequest("posts")
            val searchSourceBuilder = SearchSourceBuilder()
            val multiMatchQueryBuilder1 = MultiMatchQueryBuilder(
                request.typedText,
                "title",
                "description",
                "userHandle",
                "userName",
                "userMobile",
                "userProfile",
                "tags.displayName",
                "categories",
                "zipcode",
                "locationName"
            )
            multiMatchQueryBuilder1.operator(Operator.OR);
            searchSourceBuilder.from(request.from)
            searchSourceBuilder.size(request.size)
            searchSourceBuilder.query(multiMatchQueryBuilder1);
            searchRequest.source(searchSourceBuilder)
            val searchResponse: SearchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)

            val posts = searchResponse.hits.hits.map {
                it.getESPost()
            }.filterNotNull().filter { it.media != null }
            PostsSearchResponse(
                posts = posts.map { it.getPost() },
                typedText = request.typedText,
                from = request.from,
                size = request.size,
                numFound = searchResponse.hits.totalHits?.value ?: posts.size.toLong()
            )
        } catch (e: Exception) {
            logger.error("Failed to get Search Results for typedText: ${request.typedText}")
            e.printStackTrace()
            null
        }
    }

}
