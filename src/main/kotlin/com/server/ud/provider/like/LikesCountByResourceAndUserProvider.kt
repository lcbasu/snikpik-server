package com.server.ud.provider.like

import com.server.ud.dao.like.LikesCountByResourceAndUserRepository
import com.server.ud.entities.like.LikesCountByResourceAndUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LikesCountByResourceAndUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likesCountByResourceAndUserRepository: LikesCountByResourceAndUserRepository

    fun getLikesCountByResourceAndUser(resourceId: String, userId: String): LikesCountByResourceAndUser? =
    try {
        val resourceLikes = likesCountByResourceAndUserRepository.findAllByResourceAndUserId(resourceId, userId)
        if (resourceLikes.size > 1) {
            error("More than one likes has same resourceId: $resourceId by the userId: $userId")
        }
        resourceLikes.firstOrNull()
    } catch (e: Exception) {
        logger.error("Getting LikesCountByResourceAndUser for $resourceId & userId: $userId failed.")
        e.printStackTrace()
        null
    }

    fun increaseLike(resourceId: String, userId: String) {
        likesCountByResourceAndUserRepository.incrementLikes(resourceId, userId)
        logger.warn("Increased like for resourceId: $resourceId & userId: $userId")
    }
    fun decreaseLike(resourceId: String, userId: String) {
        val existing = getLikesCountByResourceAndUser(resourceId, userId)
        if (existing?.likesCount != null && existing.likesCount!! > 0) {
            likesCountByResourceAndUserRepository.decrementLikes(resourceId, userId)
            logger.warn("Decreased like for resourceId: $resourceId & userId: $userId")
        } else {
            logger.warn("The likes count is already zero. So skipping decreasing it further for resourceId: $resourceId & userId: $userId")
        }
    }

    fun resetLikes(resourceId: String, userId: String) =
        likesCountByResourceAndUserRepository.setLikesCount(resourceId, userId, 0)

}
