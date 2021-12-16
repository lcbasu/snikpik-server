package com.server.ud.provider.search

import com.algolia.search.SearchClient
import com.server.ud.dto.PostsSearchResponse
import com.server.ud.dto.UDSearchRequest
import com.server.ud.entities.post.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.elasticsearch.client.RestHighLevelClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SearchProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var restHighLevelClient: RestHighLevelClient

    @Autowired
    private lateinit var searchClient: SearchClient

    fun getPostsForSearchText(request: UDSearchRequest): PostsSearchResponse? {
        return null
//        return try {
//            val searchRequest = SearchRequest("posts")
//            val searchSourceBuilder = SearchSourceBuilder()
//            val multiMatchQueryBuilder1 = MultiMatchQueryBuilder(
//                request.typedText,
//                "title",
//                "description",
//                "userHandle",
//                "userName",
//                "userMobile",
//                "userProfile",
//                "tags.displayName",
//                "categories",
//                "zipcode",
//                "locationName"
//            )
//            multiMatchQueryBuilder1.operator(Operator.OR);
//            searchSourceBuilder.from(request.from)
//            searchSourceBuilder.size(request.size)
//            searchSourceBuilder.query(multiMatchQueryBuilder1);
//            searchRequest.source(searchSourceBuilder)
//            val searchResponse: SearchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
//
//            val posts = searchResponse.hits.hits.map {
//                it.getESPost()
//            }.filterNotNull().filter { it.media != null }
//            PostsSearchResponse(
//                posts = posts.map { it.getPost() },
//                typedText = request.typedText,
//                from = request.from,
//                size = request.size,
//                numFound = searchResponse.hits.totalHits?.value ?: posts.size.toLong()
//            )
//        } catch (e: Exception) {
//            logger.error("Failed to get Search Results for typedText: ${request.typedText}")
//            e.printStackTrace()
//            null
//        }
    }

    fun doSearchProcessingForPost(post: Post) {
        GlobalScope.launch {
            val postSave = async { savePostToAlgolia(post) }
            val postAutoSuggestSave = async { saveAutoSuggestForPostToAlgolia(post) }
            postSave.await()
            postAutoSuggestSave.await()
        }
    }

    private fun savePostToAlgolia(post: Post) {
        GlobalScope.launch {
            val index = searchClient.initIndex("posts", AlgoliaPost::class.java)
            index.saveObject(post.toAlgoliaPost())
        }
    }

    fun saveAutoSuggestForPostToAlgolia(post: Post) {
        GlobalScope.launch {
            val index = searchClient.initIndex("posts_query_suggestions", AlgoliaPostAutoSuggest::class.java)
            post.toAlgoliaPostAutoSuggest().map {
                async { index.saveObject(it) }
            }.map { it.await() }
        }
    }

    fun deletePost(postId: String) {
        GlobalScope.launch {
            val index = searchClient.initIndex("posts", AlgoliaPost::class.java)
            index.deleteObject(postId)
        }
    }

}
