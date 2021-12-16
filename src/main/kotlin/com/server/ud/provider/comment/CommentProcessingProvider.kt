package com.server.ud.provider.comment

import com.server.ud.dao.comment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var commentForPostByUserRepository: CommentForPostByUserRepository

    @Autowired
    private lateinit var commentsByPostRepository: CommentsByPostRepository

    @Autowired
    private lateinit var commentsByUserRepository: CommentsByUserRepository

    @Autowired
    private lateinit var commentsCountByPostRepository: CommentsCountByPostRepository

    fun processComment(commentId: String) {
        GlobalScope.launch {
            logger.info("Start: comment processing for commentId: $commentId")
            val postComment = commentProvider.getComment(commentId) ?: error("Failed to get comments data for commentId: $commentId")
            val commentsByPostFuture = async { commentsByPostProvider.save(postComment) }
            val commentsByUserFuture = async { commentsByUserProvider.save(postComment) }
            val commentsCountByPostFuture = async { commentsCountByPostProvider.increaseCommentCount(postComment.postId) }
            val commentForPostByUserFuture = async { commentForPostByUserProvider.setCommented(postComment.postId, postComment.userId) }
            commentsByPostFuture.await()
            commentsByUserFuture.await()
            commentsCountByPostFuture.await()
            commentForPostByUserFuture.await()
            logger.info("Done: comment processing for commentId: $commentId")
        }
    }

    fun deletePost(postId: String) {
        GlobalScope.launch {
            commentRepository.deleteAll(commentRepository.findAllByPostId(postId))
            commentForPostByUserRepository.deleteAll(commentForPostByUserRepository.findAllByPostId(postId))
            commentsByPostRepository.deleteAll(commentsByPostRepository.findAllByPostId_V2(postId))
            commentsByUserRepository.deleteAll(commentsByUserRepository.findAllByPostId(postId))
            commentsCountByPostRepository.deleteAll(commentsCountByPostRepository.findAllByPostId(postId))
        }
    }
}
