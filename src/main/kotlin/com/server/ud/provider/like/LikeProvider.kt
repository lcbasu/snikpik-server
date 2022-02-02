package com.server.ud.provider.like

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.RandomIdProvider
import com.server.ud.dao.like.LikeRepository
import com.server.ud.dto.ResourceLikesReportDetail
import com.server.ud.dto.ResourceLikesReportDetailForUser
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like
import com.server.ud.enums.LikeUpdateAction
import com.server.ud.provider.deferred.DeferredProcessingProvider
import com.server.ud.provider.job.UDJobProvider
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
    private lateinit var randomIdProvider: RandomIdProvider

    @Autowired
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var likesCountByResourceProvider: LikesCountByResourceProvider

    @Autowired
    private lateinit var likeForResourceByUserProvider: LikeForResourceByUserProvider

    @Autowired
    private lateinit var likeProcessingProvider: LikeProcessingProvider


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

    fun save(userId: String, request: SaveLikeRequest) : Like? {
        try {
            // Not checking uniqueness of id
            // As we are ok if one or two miss happens
            // As like is very high frequency call
            // So checking uniqueness will increase the latency
            val like = Like(
                likeId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.LIK),
                userId = userId,
                resourceId = request.resourceId,
                resourceType = request.resourceType,
                liked = request.action == LikeUpdateAction.ADD
            )
            val savedLike = likeRepository.save(like)
            likeProcessingProvider.thingsToDoForLikeProcessingNow(savedLike)
            udJobProvider.scheduleProcessingForLike(savedLike.likeId)
            return savedLike
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getResourceLikesDetail(resourceId: String, userId: String): ResourceLikesReportDetail {
        val likesCountByResource = likesCountByResourceProvider.getLikesCountByResource(resourceId)?.likesCount ?: 0
        val liked = likeForResourceByUserProvider.getLikeForResourceByUser(
            resourceId = resourceId,
            userId = userId
        )?.liked ?: false
        return ResourceLikesReportDetail(
            resourceId = resourceId,
            likes = likesCountByResource,
            userLevelInfo = ResourceLikesReportDetailForUser(
                userId = userId,
                liked = liked
            )
        )
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
