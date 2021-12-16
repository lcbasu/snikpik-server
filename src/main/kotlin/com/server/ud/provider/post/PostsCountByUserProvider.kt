package com.server.ud.provider.post

import com.server.ud.dao.post.PostsCountByUserRepository
import com.server.ud.entities.user.PostsCountByUser

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostsCountByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsCountByUserRepository: PostsCountByUserRepository

    fun getPostsCountByUser(userId: String): PostsCountByUser? =
        try {
            val userPosts = postsCountByUserRepository.findAllByUserId(userId)
            if (userPosts.size > 1) {
                error("More than one posts count has same userId: $userId")
            } else if (userPosts.isEmpty()) {
                val defaultPostCount = PostsCountByUser()
                defaultPostCount.userId = userId
                defaultPostCount.postsCount = 0
                defaultPostCount
            } else {
                userPosts.firstOrNull()
            }
        } catch (e: Exception) {
            logger.error("Getting PostsCountByUser for $userId failed.")
            e.printStackTrace()
            null
        }

    fun increasePostCount(userId: String) {
        postsCountByUserRepository.incrementPostCount(userId)
        logger.warn("Increased post count for userId: $userId")
    }

    fun decrementPostCount(userId: String) {
        val existing = getPostsCountByUser(userId)
        if (existing?.postsCount != null && existing.postsCount!! > 0) {
            postsCountByUserRepository.decrementPostCount(userId)
            logger.warn("Decreased posts count for userId: $userId")
        } else {
            logger.warn("The posts count is already zero. So skipping decreasing it further for userId: $userId")
        }
    }


    fun deletePost(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
