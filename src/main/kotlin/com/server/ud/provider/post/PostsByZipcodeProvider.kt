package com.server.ud.provider.post

import com.server.ud.dao.post.PostsByZipcodeRepository
import com.server.ud.dao.post.PostsByZipcodeTrackerRepository
import com.server.ud.entities.post.*
import com.server.ud.enums.ProcessingType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PostsByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByZipcodeRepository: PostsByZipcodeRepository

    @Autowired
    private lateinit var postsByZipcodeTrackerRepository: PostsByZipcodeTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(post: Post): PostsByZipcode? {
        try {
            if (post.zipcode == null) {
                logger.error("Post does not have location zipcode. Hence unable to save into PostsByZipcode for postId: ${post.postId}.")
                return null
            }
            val postsByZipcode = PostsByZipcode(
                zipcode = post.zipcode!!,
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                userId = post.userId,
                locationId = post.locationId,
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
            val saved = postsByZipcodeRepository.save(postsByZipcode)
            postsByZipcodeTrackerRepository.save(saved.toPostsByZipcodeTracker())
            return saved
        } catch (e: Exception) {
            logger.error("Saving PostsByZipcode failed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getAllPostsTracker(postId: String): List<PostsByZipcodeTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<PostsByZipcodeTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = postsByZipcodeTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = postsByZipcodeTrackerRepository.findAllByPostId(
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

    fun getAllByPostId(postId: String) : List<PostsByZipcode> {
        val trackedPosts = getAllPostsTracker(postId)
        return trackedPosts.map {
            it.toPostsByZipcode()
        }
    }

    fun deletePostExpandedData(postId: String) {
        val all = getAllByPostId(postId)
        all.chunked(10).forEach {
            postsByZipcodeRepository.deleteAll(it)
        }
    }

    fun processPostExpandedData(post: Post) {
        save(post)
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            when (processingType) {
                ProcessingType.NO_PROCESSING -> logger.error("This should not happen. Updating the zipcode without processing should never happen.")
                ProcessingType.DELETE_AND_REFRESH -> {
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
