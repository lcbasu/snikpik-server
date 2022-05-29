package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByDateRepository
import com.server.ud.dao.post.PostsByDateTrackerRepository
import com.server.ud.dto.AllPostsForDateRequest
import com.server.ud.dto.AllPostsForDateResponse
import com.server.ud.dto.toSavedPostResponse
import com.server.ud.entities.post.*
import com.server.ud.enums.ProcessingType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.UDCommonUtils
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PostsByDateProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByDateRepository: PostsByDateRepository

    @Autowired
    private lateinit var postsByDateTrackerRepository: PostsByDateTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(post: Post): PostsByDate? {
        try {
            val postsByDate = PostsByDate(
                forDate = DateUtils.toStringForDate(DateUtils.getInstantDateTime(post.createdAt)),
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
            val saved = postsByDateRepository.save(postsByDate)

            postsByDateTrackerRepository.save(saved.toPostsByDateTracker())

            return saved
        } catch (e: Exception) {
            logger.error("Saving PostsByDate failed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getTotalPostsForDateResponse(forDate: String): List<PostsByDate> {
        val limit = 10
        var pagingState = ""

        val resultPosts = mutableListOf<PostsByDate>()

        val slicedResult = getAllPostForDateInternal(AllPostsForDateRequest(
            forDate = forDate,
            limit = limit,
            pagingState = UDCommonUtils.DEFAULT_PAGING_STATE_VALUE
        ))
        resultPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: UDCommonUtils.DEFAULT_PAGING_STATE_VALUE
        while (hasNext) {
            val nextSlicedResult = getAllPostForDateInternal(AllPostsForDateRequest(
                forDate = forDate,
                limit = limit,
                pagingState = pagingState
            ))
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: UDCommonUtils.DEFAULT_PAGING_STATE_VALUE
            resultPosts.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return resultPosts
    }

    fun getAllPostForDate(request: AllPostsForDateRequest): AllPostsForDateResponse {
        val result = getAllPostForDateInternal(request)
        return AllPostsForDateResponse(
            forDate = request.forDate,
            posts = result.content?.filterNotNull()?.map { it.toSavedPostResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    private fun getAllPostForDateInternal(request: AllPostsForDateRequest): CassandraPageV2<PostsByDate> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val posts = postsByDateRepository.findAllByForDate(request.forDate, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun getAllPostsTracker(postId: String): List<PostsByDateTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<PostsByDateTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = postsByDateTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        while (hasNext) {
            pagingState = slicedResult.pagingState ?: ""
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = postsByDateTrackerRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            trackedPosts.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedPosts
    }

    fun getAllByPostId(postId: String) : List<PostsByDate> {
        val trackedPosts = getAllPostsTracker(postId)
        val posts = mutableListOf<PostsByDate>()
        return trackedPosts.map {
            it.toPostsByDate()
//            posts.addAll(
//                postsByDateRepository.findAllByHashTagIdAndPostTypeAndCreatedAtAndPostIdAndUserId(
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
            postsByDateRepository.deleteAll(it)
        }
    }

    fun processPostExpandedData(post: Post) {
        save(post)
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
