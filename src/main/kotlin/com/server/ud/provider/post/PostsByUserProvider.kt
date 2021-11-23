package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByUserRepository
import com.server.ud.dto.PostsByUserRequest
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
                createdAt = DateUtils.getInstantNow(),
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
}
