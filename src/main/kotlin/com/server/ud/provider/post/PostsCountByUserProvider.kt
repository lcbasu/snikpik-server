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
                error("More than one posts has same userId: $userId")
            }
            userPosts.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting PostsCountByUser for $userId failed.")
            e.printStackTrace()
            null
        }

    fun increaseCommentCount(userId: String) {
        postsCountByUserRepository.incrementPostCount(userId)
        logger.warn("Increased comment for userId: $userId")
    }
}
