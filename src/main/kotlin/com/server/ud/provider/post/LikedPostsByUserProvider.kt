package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.LikedPostsByUserRepository
import com.server.ud.dto.LikedPostsByUserRequest
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.LikedPostsByUser
import com.server.ud.entities.post.Post
import com.server.ud.enums.PostType
import com.server.ud.enums.ResourceType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class LikedPostsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likedPostsByUserRepository: LikedPostsByUserRepository

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun processLike(like: Like): LikedPostsByUser? {
        try {
            if (like.resourceType != ResourceType.POST) {
                return null
            }
            val post = postProvider.getPost(like.resourceId) ?: error("Error while getting post with postId: ${like.resourceId}")
            return save(like, post)
        } catch (e: Exception) {
            logger.error("Saving LikedPostsByUser failed for likeId: ${like.liked}.")
            e.printStackTrace()
            return null
        }
    }

    fun save(like: Like, post: Post): LikedPostsByUser? {
        try {
            val likedPostByUser = LikedPostsByUser(
                userId = like.userId,
                liked = like.liked,
                createdAt = DateUtils.getInstantNow(),
                postCreatedAt = post.createdAt,
                postedByUserId = post.userId,
                postId = post.postId,
                postType = post.postType,
                title = post.title,
                description = post.description,
                media = post.media,
                tags = post.tags,
                categories = post.categories,
            )
            return likedPostsByUserRepository.save(likedPostByUser)
        } catch (e: Exception) {
            logger.error("Saving LikedPostsByUser failed for likeId: ${like.likeId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getLikedPostsByUser(request: LikedPostsByUserRequest): CassandraPageV2<LikedPostsByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
         val posts = likedPostsByUserRepository.findAllByUserIdAndPostTypeAndLiked(request.userId, PostType.GENERIC_POST, true, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

}
