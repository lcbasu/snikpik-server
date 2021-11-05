package com.server.ud.provider.like

import kotlinx.coroutines.async
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
    private lateinit var likesCountByResourceAndUserProvider: LikesCountByResourceAndUserProvider

    @Autowired
    private lateinit var likesCountByUserProvider: LikesCountByUserProvider

    fun processLike(likeId: String) {
        runBlocking {
            logger.info("Do like processing for likeId: $likeId")
            val like = likesProvider.getLike(likeId) ?: error("Failed to get like data for likeId: $likeId")
            val likesByResourceFuture = async { likesByResourceProvider.save(like) }
            val likesByUserFuture = async { likesByUserProvider.save(like) }
            val likesCountByResourceFuture = async { if (like.liked) likesCountByResourceProvider.increaseLike(like.resourceId) else likesCountByResourceProvider.decreaseLike(like.resourceId) }
            val likesCountByResourceAndUserFuture = async { if (like.liked) likesCountByResourceAndUserProvider.increaseLike(like.resourceId, like.userId) else likesCountByResourceAndUserProvider.decreaseLike(like.resourceId, like.userId) }
            val likesCountByUserFuture = async { if (like.liked) likesCountByUserProvider.increaseLike(like.userId) else likesCountByUserProvider.decreaseLike(like.userId) }
            likesByResourceFuture.await()
            likesByUserFuture.await()
            likesCountByResourceFuture.await()
            likesCountByResourceAndUserFuture.await()
            likesCountByUserFuture.await()
        }
    }


}
