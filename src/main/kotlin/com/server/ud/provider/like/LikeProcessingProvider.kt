package com.server.ud.provider.like

import com.server.ud.provider.post.LikedPostsByUserProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
            logger.info("Start: like processing for likeId: $likeId")
            val like = likesProvider.getLike(likeId) ?: error("Failed to get like data for likeId: $likeId")
            val likesByResourceFuture = async { likesByResourceProvider.save(like) }
            val likesByUserFuture = async { likesByUserProvider.save(like) }
            val likesCountByResourceFuture = async { if (like.liked) likesCountByResourceProvider.increaseLike(like.resourceId) else likesCountByResourceProvider.decreaseLike(like.resourceId) }
            val likesCountByResourceAndUserFuture = async { likeForResourceByUserProvider.setLike(like.resourceId, like.userId, like.liked) }
            val likesCountByUserFuture = async { if (like.liked) likesCountByUserProvider.increaseLike(like.userId) else likesCountByUserProvider.decreaseLike(like.userId) }
            val likedPostsByUserProviderFuture = async { likedPostsByUserProvider.processLike(like) }
            likesByResourceFuture.await()
            likesByUserFuture.await()
            likesCountByResourceFuture.await()
            likesCountByResourceAndUserFuture.await()
            likesCountByUserFuture.await()
            likedPostsByUserProviderFuture.await()
            logger.info("Done: like processing for likeId: $likeId")
        }
    }


}
