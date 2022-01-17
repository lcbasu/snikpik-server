package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByUserRepository
import com.server.ud.dto.*
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByUser
import com.server.ud.enums.PostType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
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

    fun deletePost(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
