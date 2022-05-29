package com.server.ud.provider.post

import com.server.ud.dao.post.PostsByHashTagRepository
import com.server.ud.dao.post.PostsByHashTagTrackerRepository
import com.server.ud.entities.post.*
import com.server.ud.enums.ProcessingType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PostsByHashTagProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByHashTagRepository: PostsByHashTagRepository

    @Autowired
    private lateinit var postsByHashTagTrackerRepository: PostsByHashTagTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(post: Post, hashTagId: String): PostsByHashTag? {
        try {
            val postsByHashTag = PostsByHashTag(
                hashTagId = hashTagId,
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                userId = post.userId,
                locationId = post.locationId,
                zipcode = post.zipcode,
                locationName = post.locationName,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                title = post.title,
                description = post.description,
                media = post.media,
                sourceMedia = post.sourceMedia,
                tags = post.tags,
                categories = post.categories,
                locality = post.locality,
                subLocality = post.subLocality,
                route = post.route,
                city = post.city,
                state = post.state,
                country = post.country,
                countryCode = post.countryCode,
                completeAddress = post.completeAddress,
            )
            val saved = postsByHashTagRepository.save(postsByHashTag)

            postsByHashTagTrackerRepository.save(saved.toPostsByHashTagTracker())

            return saved
        } catch (e: Exception) {
            logger.error("Saving PostsByHashTag failed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }


    fun getAllPostsTracker(postId: String): List<PostsByHashTagTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<PostsByHashTagTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = postsByHashTagTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = postsByHashTagTrackerRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedPosts.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedPosts
    }

    fun getAllByPostId(postId: String) : List<PostsByHashTag> {
        val trackedPosts = getAllPostsTracker(postId)
        val posts = mutableListOf<PostsByHashTag>()
        return trackedPosts.map {
            it.toPostsByHashTag()
//            posts.addAll(
//                postsByHashTagRepository.findAllByHashTagIdAndPostTypeAndCreatedAtAndPostIdAndUserId(
//                    it.hashTagId,
//                    it.postType,
//                    it.createdAt,
//                    it.postId,
//                    it.userId
//                )
//            )
        }
//        return posts
    }

    fun deletePostExpandedData(postId: String) {
        val all = getAllByPostId(postId)
        all.chunked(10).forEach {
            postsByHashTagRepository.deleteAll(it)
        }
    }

    fun processPostExpandedData(post: Post) {
        runBlocking {
            post.getHashTags().tags
                .map { async { save(post, it) } }
                .map { it.await() }
        }
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            when (processingType) {
                ProcessingType.NO_PROCESSING -> logger.error("This should not happen. Updating the hash-tags without processing should never happen.")
                ProcessingType.DELETE_AND_REFRESH -> {
                    // Do not run delete and update in parallel
                    // Delete old data
                    deletePostExpandedData(postUpdate.oldPost.postId)
                    // Index the new data
                    processPostExpandedData(postUpdate.newPost!!)
                }
                ProcessingType.REFRESH -> {
                    // Index the new data
                    processPostExpandedData(postUpdate.newPost!!)
                }
            }
        }
    }
}
