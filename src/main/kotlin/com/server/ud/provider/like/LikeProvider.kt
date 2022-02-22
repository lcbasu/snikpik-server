package com.server.ud.provider.like

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.ud.dao.like.*
import com.server.ud.dto.ResourceLikesReportDetail
import com.server.ud.dto.ResourceLikesReportDetailForUser
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like
import com.server.ud.enums.LikeUpdateAction
import com.server.ud.enums.ResourceType
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.post.LikedPostsByUserProvider
import com.server.ud.provider.user_activity.UserActivitiesProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var likesCountByResourceProvider: LikesCountByResourceProvider

    @Autowired
    private lateinit var likeForResourceByUserProvider: LikeForResourceByUserProvider

    @Autowired
    private lateinit var likesByResourceProvider: LikesByResourceProvider

    @Autowired
    private lateinit var likesByUserProvider: LikesByUserProvider

    @Autowired
    private lateinit var likesCountByUserProvider: LikesCountByUserProvider

    @Autowired
    private lateinit var likedPostsByUserProvider: LikedPostsByUserProvider

    @Autowired
    private lateinit var likeForResourceByUserRepository: LikeForResourceByUserRepository

    @Autowired
    private lateinit var likesByResourceRepository: LikesByResourceRepository

    @Autowired
    private lateinit var likesByUserRepository: LikesByUserRepository

    @Autowired
    private lateinit var likesCountByResourceRepository: LikesCountByResourceRepository

    @Autowired
    private lateinit var likesCountByUserRepository: LikesCountByUserRepository

    @Autowired
    private lateinit var userActivitiesProvider: UserActivitiesProvider

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
                likeId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.LIK.name),
                userId = userId,
                resourceId = request.resourceId,
                resourceType = request.resourceType,
                liked = request.action == LikeUpdateAction.ADD
            )
            val savedLike = likeRepository.save(like)
            thingsToDoForLikeProcessingNow(savedLike)
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

    fun processLike(likeId: String) {
        GlobalScope.launch {
            logger.info("Later:Start: like processing for likeId: $likeId")
            val like = getLike(likeId) ?: error("Failed to get like data for likeId: $likeId")
            val likedPostsByUserProviderFuture = async { likedPostsByUserProvider.processLike(like) }
            val likesByResourceFuture = async { likesByResourceProvider.save(like) }
            val likesByUserFuture = async { likesByUserProvider.save(like) }
            val userActivityFuture = async {
                if (like.liked) {
                    userActivitiesProvider.saveLikeLevelActivity(like)
                } else {
                    userActivitiesProvider.deleteLikeLevelActivity(like)
                }
            }
            likedPostsByUserProviderFuture.await()
            likesByResourceFuture.await()
            likesByUserFuture.await()
            userActivityFuture.await()
            logger.info("Later:Done: like processing for likeId: $likeId")
        }
    }

    fun processAllLikes() {
        GlobalScope.launch {
            logger.info("Start processAllLikes")
            val likes = likeRepository.findAll()
            likes.filterNotNull().forEach {
                likesByResourceProvider.save(it)
            }
            logger.info("Done processAllLikes")
        }
    }

    fun thingsToDoForLikeProcessingNow(like: Like) {
        runBlocking {
            logger.info("Now:Start: like processing for likeId: ${like.likeId}")

            // Check if already liked or not
            val liked = likeForResourceByUserProvider.getLikeForResourceByUser(
                resourceId = like.resourceId,
                userId = like.userId
            )?.liked ?: false

            // If the previous and current state are not same, then update the likes count
            if (liked != like.liked) {
                val likeForResourceByUserFuture = async { likeForResourceByUserProvider.setLike(like.resourceId, like.userId, like.liked) }
                val likesCountByResourceFuture = async { if (like.liked) likesCountByResourceProvider.increaseLike(like.resourceId) else likesCountByResourceProvider.decreaseLike(like.resourceId) }
                val likesCountByUserFuture = async { if (like.liked) likesCountByUserProvider.increaseLike(like.userId) else likesCountByUserProvider.decreaseLike(like.userId) }
                likeForResourceByUserFuture.await()
                likesCountByResourceFuture.await()
                likesCountByUserFuture.await()
            }
            logger.info("Now:Done: like processing for likeId: ${like.likeId}")
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            val likesByResource = likesByResourceRepository.findAllByResourceId(postId)
            val firstValue = likesByResource.firstOrNull()
            val userIds = likesByResource.map { it.userId }.toSet()
            val likeIds = likesByResource.mapNotNull { it.likeId }.toSet()
            likeIds.map {
                async { likeRepository.deleteByLikeId(it) }
            }.map {
                it.await()
            }
            userIds.filterNotNull().map {
                async {
                    val liked = likeForResourceByUserProvider.getLikeForResourceByUser(
                        resourceId = postId,
                        userId = it
                    )?.liked ?: false

                    if (liked) {
                        likesCountByUserRepository.decrementLikes(it)
                    }
                    likeForResourceByUserRepository.deleteAllByResourceIdAndUserId(postId, it)

                    // TODO: Optimize this
                    val allUserLikes = likesByUserRepository.findAllByResourceId(postId)
                    likesByUserRepository.deleteAll(allUserLikes)
                }
            }.map {
                it.await()
            }
            likesCountByResourceRepository.deleteAllByResourceId(postId)
            firstValue?.let {
                likesByResourceRepository.deleteAllByResourceIdAndResourceType(postId, it.resourceType)
            }
        }
    }
}
