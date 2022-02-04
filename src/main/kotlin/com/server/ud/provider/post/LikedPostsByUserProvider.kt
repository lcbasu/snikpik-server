package com.server.ud.provider.post

import com.server.ud.dao.post.LikedPostsByUserRepository
import com.server.ud.dto.LikedPostsByUserRequest
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.LikedPostsByUser
import com.server.ud.entities.post.Post
import com.server.ud.enums.ResourceType
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
class LikedPostsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likedPostsByUserRepository: LikedPostsByUserRepository

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun processLike(like: Like): LikedPostsByUser? {
        return try {
            if (like.resourceType == ResourceType.POST || like.resourceType == ResourceType.WALL) {
                val post = postProvider.getPost(like.resourceId) ?: error("Error while getting post with postId: ${like.resourceId}")
                if (like.liked) {
                    save(like, post)
                } else {
                    likedPostsByUserRepository.deleteByUserIdAndPostTypeAndPostId(
                        userId = like.userId,
                        postType = post.postType,
                        postId = like.resourceId,
                    )
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Saving LikedPostsByUser failed for likeId: ${like.liked}.")
            e.printStackTrace()
            null
        }
    }

    fun save(like: Like, post: Post): LikedPostsByUser? {
        try {
            val likedPostByUser = LikedPostsByUser(
                userId = like.userId,
                createdAt = like.createdAt,
                postCreatedAt = post.createdAt,
                postedByUserId = post.userId,
                postId = post.postId,
                postType = post.postType,
                title = post.title,
                description = post.description,
                media = post.media,
                sourceMedia = post.sourceMedia,
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
         val posts = likedPostsByUserRepository.findAllByUserId(request.userId, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            likedPostsByUserRepository.deleteAll(likedPostsByUserRepository.findAllByPostId(postId))
        }
    }

}
