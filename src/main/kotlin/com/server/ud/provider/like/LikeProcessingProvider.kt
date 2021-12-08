package com.server.ud.provider.like

import com.server.ud.entities.like.Like
import com.server.ud.provider.post.LikedPostsByUserProvider
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

    fun processLike(likeId: String) {
        GlobalScope.launch {
            val like = likesProvider.getLike(likeId) ?: error("Failed to get like data for likeId: $likeId")
            logger.info("Later:Start: like processing for likeId: ${like.likeId}")
            val likedPostsByUserProviderFuture = async { likedPostsByUserProvider.processLike(like) }
            val likesByResourceFuture = async { likesByResourceProvider.save(like) }
            val likesByUserFuture = async { likesByUserProvider.save(like) }
            likedPostsByUserProviderFuture.await()
            likesByResourceFuture.await()
            likesByUserFuture.await()
            logger.info("Later:Done: like processing for likeId: ${like.likeId}")
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

                val likeForResourceByUserFuture = async {
                    likeForResourceByUserProvider.setLike(like.resourceId, like.userId, like.liked)
                }

                val likesCountByResourceFuture = async {
                    if (like.liked) likesCountByResourceProvider.increaseLike(like.resourceId) else likesCountByResourceProvider.decreaseLike(
                        like.resourceId
                    )
                }

                val likesCountByUserFuture = async {
                    if (like.liked) likesCountByUserProvider.increaseLike(like.userId) else likesCountByUserProvider.decreaseLike(
                        like.userId
                    )
                }

                likeForResourceByUserFuture.await()
                likesCountByResourceFuture.await()
                likesCountByUserFuture.await()
            }
            logger.info("Now:Done: like processing for likeId: ${like.likeId}")
        }
    }
}
