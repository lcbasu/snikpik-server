package com.server.ud.provider.comment

import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.comment.*
import com.server.ud.dto.SaveCommentRequest
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.comment.Comment
import com.server.ud.provider.job.UDJobProvider
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
class CommentProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var commentsByPostProvider: CommentsByPostProvider

    @Autowired
    private lateinit var commentsByUserProvider: CommentsByUserProvider

    @Autowired
    private lateinit var commentsCountByPostProvider: CommentsCountByPostProvider

    @Autowired
    private lateinit var commentForPostByUserProvider: CommentForPostByUserProvider

    @Autowired
    private lateinit var commentForPostByUserRepository: CommentForPostByUserRepository

    @Autowired
    private lateinit var commentsByPostRepository: CommentsByPostRepository

    @Autowired
    private lateinit var commentsByUserRepository: CommentsByUserRepository

    @Autowired
    private lateinit var commentsCountByPostRepository: CommentsCountByPostRepository

    @Autowired
    private lateinit var userActivitiesProvider: UserActivitiesProvider


    fun getComment(commentId: String): Comment? =
        try {
            val comments = commentRepository.findAllByCommentId(commentId)
            if (comments.size > 1) {
                error("More than one comment has same commentId: $commentId")
            }
            comments.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting PostComment for $commentId failed.")
            e.printStackTrace()
            null
        }

    fun save(userId: String, request: SaveCommentRequest) : Comment? {
        try {
            val comment = Comment(
                commentId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.CMT.name),
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                postType = request.postType,
                postId = request.postId,
                text = request.text,
                media = request.mediaDetails?.convertToString(),
            )
            val savedComment = commentRepository.save(comment)
            processCommentNow(savedComment)
            udJobProvider.scheduleProcessingForComment(savedComment.commentId)
            return savedComment
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun handleProcessedMedia(updatedMediaDetail: MediaProcessingDetail) {
        TODO("Not yet implemented")
    }


    fun processComment(commentId: String) {
        GlobalScope.launch {
            logger.info("Start: comment processing for commentId: $commentId")
            val postComment = getComment(commentId) ?: error("Failed to get comments data for commentId: $commentId")
            val commentsByUserFuture = async { commentsByUserProvider.save(postComment) }
            val commentForPostByUserFuture = async { commentForPostByUserProvider.setCommented(postComment.postId, postComment.userId) }
            val userActivityFuture = async {
                userActivitiesProvider.saveCommentCreationActivity(postComment)
            }
            commentsByUserFuture.await()
            commentForPostByUserFuture.await()
            userActivityFuture.await()
            logger.info("Done: comment processing for commentId: $commentId")
        }
    }

    fun processCommentNow(comment: Comment) {
        runBlocking {
            logger.info("StartNow: comment processing for commentId: ${comment.commentId}")
            val commentsByPostFuture = async { commentsByPostProvider.save(comment) }
            val commentsCountByPostFuture = async { commentsCountByPostProvider.increaseCommentCount(comment.postId) }
            commentsByPostFuture.await()
            commentsCountByPostFuture.await()
            logger.info("DoneNow: comment processing for commentId: ${comment.commentId}")
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            commentRepository.deleteAll(commentRepository.findAllByPostId(postId))
            commentForPostByUserRepository.deleteAll(commentForPostByUserRepository.findAllByPostId(postId))
            commentsByPostRepository.deleteAll(commentsByPostRepository.findAllByPostId_V2(postId))
            commentsByUserRepository.deleteAll(commentsByUserRepository.findAllByPostId(postId))
            commentsCountByPostRepository.deleteAll(commentsCountByPostRepository.findAllByPostId(postId))
        }
    }

}
