package com.server.ud.provider.post

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.server.common.utils.CommonUtils.STRING_SEPARATOR
import com.server.dk.cache.CitiesDataCacheLoader
import com.server.ud.dao.post.PostsByCategoryRepository
import com.server.ud.dto.ExploreFeedRequest
import com.server.ud.dto.ExploreTabViewResponse
import com.server.ud.dto.toSavedPostResponse
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.entities.post.getCategories
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.UDCommonUtils.DEFAULT_PAGING_STATE_VALUE
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@Component
class PostsByCategoryProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByCategoryRepository: PostsByCategoryRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    private val exploreTabViewResponseCache by lazy {
        Caffeine
            .newBuilder()
            .maximumSize(1000) // Store 1K keys
            .expireAfterWrite(1, TimeUnit.DAYS)
            .buildAsync(ExploreTabViewResponseCacheLoader(this))
    }

    fun save(post: Post, categoryId: CategoryV2): PostsByCategory? {
        try {
            val postsByZipcode = PostsByCategory(
                categoryId = categoryId,
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                userId = post.userId,
                locationId = post.locationId,
                zipcode = post.zipcode,
                locationName = post.locationName,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                locality = post.locality,
                subLocality = post.subLocality,
                route = post.route,
                city = post.city,
                state = post.state,
                country = post.country,
                countryCode = post.countryCode,
                completeAddress = post.completeAddress,
                title = post.title,
                description = post.description,
                media = post.media,
                sourceMedia = post.sourceMedia,
                tags = post.tags,
                categories = post.categories,
            )
            return postsByCategoryRepository.save(postsByZipcode)
        } catch (e: Exception) {
            logger.error("Saving _root_ide_package_.com.server.ud.entities.post.PostsByCategory filed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getExploreTabViewResponse(request: ExploreFeedRequest): ExploreTabViewResponse {
        return try {
            getExploreTabViewResponseFromDB(request)
            // Figure out how to invalidate the cache when the post is deleted and then uncomment the below lines


//            exploreTabViewResponseCache.get(PostsByCategoryCacheKeyBuilder.getKey(request)).get() ?:
//            ExploreTabViewResponse(
//                posts = emptyList(),
//                count = 0,
//                hasNext = true,
//                pagingState = request.pagingState
//            )
        } catch (e: Exception) {
            logger.error("Error while getting getExploreTabViewResponse data from cache.")
            e.printStackTrace()
            ExploreTabViewResponse(
                posts = emptyList(),
                count = 0,
                hasNext = true,
                pagingState = request.pagingState
            )
        }
    }

    fun getExploreTabViewResponseFromDB(request: ExploreFeedRequest): ExploreTabViewResponse {
        val result = getFeedForCategory(request)
        return ExploreTabViewResponse(
            posts = result.content?.filterNotNull()?.map { it.toSavedPostResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    private fun getFeedForCategory(request: ExploreFeedRequest): CassandraPageV2<PostsByCategory> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
         val posts = postsByCategoryRepository.findAllByCategoryIdAndPostType(request.category, PostType.GENERIC_POST, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun deletePostExpandedData(post: Post) {
        GlobalScope.launch {
            val categories = (post.getCategories().categories.map { it.id } + CategoryV2.ALL).toSet()
            val posts = mutableListOf<PostsByCategory>()
            categories.map {
                val categoryV2 = it
                val postType = post.postType
                val createdAt = post.createdAt
                val postId = post.postId
                posts.addAll(postsByCategoryRepository.findAllByCategoryIdAndPostTypeAndCreatedAtAndPostId(categoryV2, postType, createdAt, postId))
            }
            posts.chunked(5).forEach {
                postsByCategoryRepository.deleteAll(it)
            }
        }
    }

    fun updatePostExpandedData(post: Post) {
        GlobalScope.launch {

        }
    }
}

// Cache

class ExploreTabViewResponseCacheLoader(private val postsByCategoryProvider: PostsByCategoryProvider):
    CacheLoader<String, ExploreTabViewResponse?> {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun reload(key: String, oldValue: ExploreTabViewResponse): ExploreTabViewResponse? = loadAsync(key).get()

    override fun asyncReload(key: String, oldValue: ExploreTabViewResponse, executor: Executor): CompletableFuture<ExploreTabViewResponse?> = loadAsync(key)

    override fun asyncLoad(key: String, executor: Executor): CompletableFuture<ExploreTabViewResponse?> = loadAsync(key)

    override fun load(key: String): ExploreTabViewResponse? = loadAsync(key).get()

    private fun loadAsync(key: String): CompletableFuture<ExploreTabViewResponse?> {
        logger.info("run loadAsync for key: $key. Means there was a cache miss")
        return CoroutineScope(Dispatchers.Default).future {
            postsByCategoryProvider.getExploreTabViewResponseFromDB(PostsByCategoryCacheKeyBuilder.getRequestFromKey(key))
        }
    }
}

object PostsByCategoryCacheKeyBuilder {
    fun getKey(request: ExploreFeedRequest): String {
        return "${request.category}${STRING_SEPARATOR}${request.limit}${STRING_SEPARATOR}${request.pagingState ?: DEFAULT_PAGING_STATE_VALUE}"
    }

    fun getRequestFromKey(key: String): ExploreFeedRequest {
        val keyParts = key.split(STRING_SEPARATOR)
        return ExploreFeedRequest(
            category = CategoryV2.valueOf(keyParts[0]),
            limit = keyParts[1].toInt(),
            pagingState = keyParts[2]
        )
    }
}
