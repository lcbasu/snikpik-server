package com.server.ud.provider.comment

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommentProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentProvider: CommentProvider

    @Autowired
    private lateinit var commentsByPostProvider: CommentsByPostProvider

    @Autowired
    private lateinit var commentsByUserProvider: CommentsByUserProvider

    @Autowired
    private lateinit var commentsCountByPostProvider: CommentsCountByPostProvider

    @Autowired
    private lateinit var commentForPostByUserProvider: CommentForPostByUserProvider

    fun processComment(commentId: String) {
        runBlocking {
            logger.info("Do comment processing for commentId: $commentId")
            val postComment = commentProvider.getComment(commentId) ?: error("Failed to get comments data for commentId: $commentId")
            val commentsByPostFuture = async { commentsByPostProvider.save(postComment) }
            val commentsByUserFuture = async { commentsByUserProvider.save(postComment) }
            val commentsCountByPostFuture = async { commentsCountByPostProvider.increaseCommentCount(postComment.postId) }
            val commentForPostByUserFuture = async { commentForPostByUserProvider.setCommented(postComment.postId, postComment.userId) }
            commentsByPostFuture.await()
            commentsByUserFuture.await()
            commentsCountByPostFuture.await()
            commentForPostByUserFuture.await()
        }
    }
}
