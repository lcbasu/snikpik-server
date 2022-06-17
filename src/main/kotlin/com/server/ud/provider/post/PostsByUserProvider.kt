package com.server.ud.provider.post

import com.server.ud.dao.post.PostsByUserRepository
import com.server.ud.dao.post.PostsByUserTrackerRepository
import com.server.ud.dto.PostsByUserRequest
import com.server.ud.dto.PostsByUserResponse
import com.server.ud.dto.PostsByUserResponseV2
import com.server.ud.entities.post.*
import com.server.ud.enums.PostType
import com.server.common.enums.ProcessingType
import com.server.common.pagination.CassandraPageV2
import com.server.common.utils.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PostsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByUserRepository: PostsByUserRepository

    @Autowired
    private lateinit var postsByUserTrackerRepository: PostsByUserTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(post: Post): PostsByUser? {
        try {
            val postsByUser = PostsByUser(
                userId = post.userId,
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                title = post.title,
                description = post.description,
                media = post.media,
                sourceMedia = post.sourceMedia,
                tags = post.tags,
                categories = post.categories,
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
            )
            return save(postsByUser)
        } catch (e: Exception) {
            logger.error("Saving PostsByUser failed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun update(postsByUser: PostsByUser, updatedPost: Post): PostsByUser? {
        try {
            return save(postsByUser.copy(
                userId = updatedPost.userId,
                createdAt = updatedPost.createdAt,
                postId = updatedPost.postId,
                postType = updatedPost.postType,
                title = updatedPost.title,
                description = updatedPost.description,
                media = updatedPost.media,
                sourceMedia = updatedPost.sourceMedia,
                tags = updatedPost.tags,
                categories = updatedPost.categories,
                locationId = updatedPost.locationId,
                zipcode = updatedPost.zipcode,
                locationName = updatedPost.locationName,
                locationLat = updatedPost.locationLat,
                locationLng = updatedPost.locationLng,
                locality = updatedPost.locality,
                subLocality = updatedPost.subLocality,
                route = updatedPost.route,
                city = updatedPost.city,
                state = updatedPost.state,
                country = updatedPost.country,
                countryCode = updatedPost.countryCode,
                completeAddress = updatedPost.completeAddress,
            ))
        } catch (e: Exception) {
            logger.error("Saving PostsByUser failed for postId: ${updatedPost.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getPostsByUser(request: PostsByUserRequest): CassandraPageV2<PostsByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val posts = postsByUserRepository.findAllByUserIdAndPostType(request.userId, PostType.GENERIC_POST, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun getPostsByUserResponse(request: PostsByUserRequest): PostsByUserResponse {
        val result = getPostsByUser(request)
        return PostsByUserResponse(
            posts = result.content?.filterNotNull()?.map { it.toPostsByUserPostDetail() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    fun getPostsByUserResponseV2(request: PostsByUserRequest): PostsByUserResponseV2 {
        val result = getPostsByUser(request)
        return PostsByUserResponseV2(
            posts = result.content?.filterNotNull()?.map { it.toSavedPostResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }


    fun getAllPostsTracker(postId: String): List<PostsByUserTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<PostsByUserTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = postsByUserTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = postsByUserTrackerRepository.findAllByPostId(
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

    fun getAllByPostId(postId: String) : List<PostsByUser> {
        val trackedPosts = getAllPostsTracker(postId)
        return trackedPosts.map {
            it.toPostsByUser()
        }
    }

    fun deletePostExpandedData(postId: String) {
        val all = getAllByPostId(postId)
        all.chunked(10).forEach {
            postsByUserRepository.deleteAll(it)
        }
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            val updatedPost = postUpdate.newPost!!
            val all = getAllByPostId(updatedPost.postId)
            all.chunked(5).map {
                async { it.map { update(it, updatedPost) } }
            }.map {
                it.await()
            }
        }
    }

    fun save(postsByUser: PostsByUser): PostsByUser {
        val saved =  postsByUserRepository.save(postsByUser)
        postsByUserTrackerRepository.save(saved.toPostsByUserTracker())
        return saved
    }
}
