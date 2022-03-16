package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByUserRepository
import com.server.ud.dto.*
import com.server.ud.entities.post.*
import com.server.ud.enums.PostType
import com.server.ud.enums.ProcessingType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PostsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByUserRepository: PostsByUserRepository

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
                zipcode = post.zipcode!!,
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
            return postsByUserRepository.save(postsByUser)
        } catch (e: Exception) {
            logger.error("Saving PostsByUser filed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun update(postsByUser: PostsByUser, updatedPost: Post): PostsByUser? {
        try {
            return postsByUserRepository.save(postsByUser.copy(
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
                zipcode = updatedPost.zipcode!!,
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
            logger.error("Saving PostsByUser filed for postId: ${updatedPost.postId}.")
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

    fun getAllByPostId(postId: String) = postsByUserRepository.findAllByPostId_V2(postId)

    fun deletePostExpandedData(postId: String) {
        val all = getAllByPostId(postId)
        postsByUserRepository.deleteAll(all)
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
}
