package com.server.ud.provider.like

import com.server.ud.dao.like.*
import com.server.ud.entities.like.Like
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
class LikeProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likesProvider: LikeProvider

    @Autowired
    private lateinit var likesByResourceProvider: LikesByResourceProvider

    @Autowired
    private lateinit var likesByUserProvider: LikesByUserProvider

    @Autowired
    private lateinit var likesCountByResourceProvider: LikesCountByResourceProvider

    @Autowired
    private lateinit var likeForResourceByUserProvider: LikeForResourceByUserProvider

    @Autowired
    private lateinit var likesCountByUserProvider: LikesCountByUserProvider

    @Autowired
    private lateinit var likedPostsByUserProvider: LikedPostsByUserProvider

    @Autowired
    private lateinit var likeForResourceByUserRepository: LikeForResourceByUserRepository

    @Autowired
    private lateinit var likeRepository: LikeRepository

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

    fun processLike(likeId: String) {
        GlobalScope.launch {
            logger.info("Later:Start: like processing for likeId: $likeId")
            val like = likesProvider.getLike(likeId) ?: error("Failed to get like data for likeId: $likeId")
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

    fun deletePost(postId: String) {
        GlobalScope.launch {
            likeForResourceByUserRepository.deleteAll(likeForResourceByUserRepository.findAllByResourceId(postId))
            likeRepository.deleteAll(likeRepository.findAllByResourceId(postId))
            likesByResourceRepository.deleteAll(likesByResourceRepository.findAllByResourceId(postId))
            likesCountByResourceRepository.deleteAll(likesCountByResourceRepository.findAllByResourceId(postId))

            val allUserLikes = likesByUserRepository.findAllByResourceId(postId)
            val likedGroupedByUser = allUserLikes.groupBy { it.userId }
            likedGroupedByUser.map {
                val userId = it.key
                val userLikes = it.value
                val likedRows = userLikes.filter { it.liked }
                val unLikedRows = userLikes.filter { !it.liked }
                // Decrease like only if the post has been liked in the end
                if (likedRows.size > unLikedRows.size) {
                    likesCountByUserRepository.decrementLikes(userId)
                }
            }

            likesByUserRepository.deleteAll(allUserLikes)
        }
    }
}
