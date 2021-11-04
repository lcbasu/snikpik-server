package com.server.ud.provider.like

import com.server.ud.dao.like.LikesCountByResourceRepository
import com.server.ud.entities.like.LikesCountByResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LikesCountByResourceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likesCountByResourceRepository: LikesCountByResourceRepository

    fun getLikesCountByResource(resourceId: String): LikesCountByResource? =
    try {
        val resourceLikes = likesCountByResourceRepository.findAllByPostId(resourceId)
        if (resourceLikes.size > 1) {
            error("More than one likes has same resourceId: $resourceId")
        }
        resourceLikes.firstOrNull()
    } catch (e: Exception) {
        logger.error("Getting LikesCountByResource for $resourceId failed.")
        e.printStackTrace()
        null
    }

    fun increaseLike(resourceId: String) {
        likesCountByResourceRepository.incrementLikes(resourceId)
        logger.warn("Increased like for resourceId: $resourceId")
    }
    fun decreaseLike(resourceId: String) {
        val existing = getLikesCountByResource(resourceId)
        if (existing != null && existing.likesCount > 0) {
            likesCountByResourceRepository.decrementLikes(resourceId)
            logger.warn("Decreased like for resourceId: $resourceId")
        } else {
            logger.warn("The likes count is already zero. So skipping decreasing it further for resourceId: $resourceId")
        }
    }

    fun resetLikes(resourceId: String) =
        likesCountByResourceRepository.setLikesCount(resourceId, 0)

}
