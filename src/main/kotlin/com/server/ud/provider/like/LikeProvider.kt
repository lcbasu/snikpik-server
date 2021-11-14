package com.server.ud.provider.like

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.ud.dao.like.LikeRepository
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.LikeUpdateAction
import com.server.ud.provider.job.JobProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LikeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likeRepository: LikeRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var jobProvider: JobProvider

    fun getLike(likeId: String): Like? =
        try {
            val likes = likeRepository.findAllByLikeId(likeId)
            if (likes.size > 1) {
                error("More than one like has same likeId: $likeId")
            }
            likes.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting Like for $likeId failed.")
            e.printStackTrace()
            null
        }

    fun save(user: UserV2, request: SaveLikeRequest) : Like? {
        try {
            val like = Like(
                likeId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.LIK.name),
                userId = user.userId,
                resourceId = request.resourceId,
                resourceType = request.resourceType,
                liked = request.action == LikeUpdateAction.ADD
            )
            val savedLike = likeRepository.save(like)
            jobProvider.scheduleProcessingForLike(savedLike.likeId)
            return savedLike
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
