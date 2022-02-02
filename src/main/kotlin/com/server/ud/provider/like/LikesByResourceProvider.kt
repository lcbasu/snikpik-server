package com.server.ud.provider.like

import com.server.common.utils.DateUtils
import com.server.ud.dao.like.LikesByResourceRepository
import com.server.ud.entities.like.Like
import com.server.ud.entities.like.LikesByResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LikesByResourceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likesByResourceRepository: LikesByResourceRepository

    fun save(like: Like) : LikesByResource? {
        try {
            val likesByResource = LikesByResource(
                resourceId = like.resourceId,
                resourceType = like.resourceType,
                createdAt = like.createdAt,
                userId = like.userId,
                liked = like.liked,
            )
            val savedLikesByResource = likesByResourceRepository.save(likesByResource)
            logger.info("Saved LikesByResource into cassandra for likeId: ${like.likeId}")
            return savedLikesByResource
        } catch (e: Exception) {
            logger.error("Saving LikesByResource into cassandra failed for likeId: ${like.likeId}")
            e.printStackTrace()
            return null
        }
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
