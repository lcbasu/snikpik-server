package com.server.ud.provider.like

import com.server.common.utils.DateUtils
import com.server.ud.dao.like.LikesByUserRepository
import com.server.ud.entities.like.Like
import com.server.ud.entities.like.LikesByUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LikesByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likesByUserRepository: LikesByUserRepository

    fun save(like: Like) : LikesByUser? {
        try {
            val likesByUser = LikesByUser(
                userId = like.userId,
                createdAt = like.createdAt,
                resourceId = like.resourceId,
                resourceType = like.resourceType,
                liked = like.liked,
                likeId = like.likeId,
            )
            val savedLikesByUser = likesByUserRepository.save(likesByUser)
            logger.info("Saved LikesByUser into cassandra for likeId: ${like.likeId}")
            return savedLikesByUser
        } catch (e: Exception) {
            logger.error("Saving LikesByUser into cassandra failed for likeId: ${like.likeId}")
            e.printStackTrace()
            return null
        }
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
