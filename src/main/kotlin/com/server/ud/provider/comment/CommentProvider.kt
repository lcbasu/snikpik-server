package com.server.ud.provider.comment

import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.comment.*
import com.server.ud.dto.DeleteCommentRequest
import com.server.ud.dto.DeleteCommentResponse
import com.server.ud.dto.SaveCommentRequest
import com.server.ud.dto.UpdateCommentRequest
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.comment.Comment
import com.server.ud.provider.bookmark.BookmarkProvider
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.reply.ReplyProvider
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
    private lateinit var bookmarkProvider: BookmarkProvider

    @Autowired
    private lateinit var likeProvider: LikeProvider

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

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

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
                commentId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.CMT.name),
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                postType = request.postType,
                postId = request.postId,
                text = request.text,
                media = request.mediaDetails?.convertToString(),
            )
            val savedComment = commentRepository.save(comment)
            processCommentNowAfterSavingFirstTime(savedComment)
            return savedComment
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun delete(comment: Comment) {
        try {
            delete(comment.commentId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun delete(commentId: String) {
        try {
            commentRepository.deleteByCommentId(commentId)
        } catch (e: Exception) {
            e.printStackTrace()
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

    fun processCommentNowAfterSavingFirstTime(comment: Comment) {
        runBlocking {
            logger.info("StartNow: comment processing for commentId: ${comment.commentId}")
            udJobProvider.scheduleProcessingForComment(comment.commentId)
            val commentsByPostFuture = async { commentsByPostProvider.save(comment) }
            val commentsCountByPostFuture = async { commentsCountByPostProvider.increaseCommentCount(comment.postId) }
            commentsByPostFuture.await()
            commentsCountByPostFuture.await()
            logger.info("DoneNow: comment processing for commentId: ${comment.commentId}")
        }
    }

    fun processCommentNowAfterUpdating(comment: Comment) {
        runBlocking {
            logger.info("StartNow: comment processing for commentId: ${comment.commentId}")
            udJobProvider.scheduleProcessingForComment(comment.commentId)
            commentsByPostProvider.save(comment)
            logger.info("DoneNow: comment processing for commentId: ${comment.commentId}")
        }
    }

    fun deleteAllCommentsOfPost(postId: String) {
        GlobalScope.launch {
            val commentsByPost = commentsByPostProvider.getAllCommentsByPost(postId)
            commentsByPost.map {
                deleteCommentAndAllExpandedData(it.commentId)
            }
        }
    }

    fun deleteCommentAndAllExpandedData(commentId: String) {
        GlobalScope.launch {
            deleteCommentAndAllExpandedData(getComment(commentId) ?: error("Failed to get comment for commentId: $commentId"))
        }
    }

    private fun deleteCommentAndAllExpandedData(comment: Comment) {
        GlobalScope.launch {
            val userId = comment.userId

            bookmarkProvider.deleteResourceExpandedData(comment.commentId)
            likeProvider.deleteResourceExpandedData(comment.commentId)
            userActivitiesProvider.deleteCommentExpandedData(comment.commentId)

            val allCommentsByUser =  commentsByUserProvider.deleteAllCommentsByUser(comment)
            if (allCommentsByUser.isEmpty() || allCommentsByUser.size == 1 && allCommentsByUser.first().commentId == comment.commentId) {
                commentForPostByUserProvider.resetCommented(comment.postId, userId)
            } else {
                commentForPostByUserProvider.setCommented(comment.postId, userId)
            }

            commentsCountByPostProvider.decreaseCommentCount(comment.postId)

            commentsByPostProvider.delete(comment)

            replyProvider.deleteAllRepliesOfComment(comment.commentId)

            delete(comment)
        }
    }

    fun deleteComment(request: DeleteCommentRequest): DeleteCommentResponse {
        val userDetailsFromToken = securityProvider.validateRequest()
        val comment = getComment(request.commentId) ?: error("Failed to get comment for commentId: ${request.commentId}")
        if (comment.userId != userDetailsFromToken.getUserIdToUse()) {
            error("You are not allowed to delete this comment. Only the owner can delete the comment. commentId: ${request.commentId}, ownerId: ${comment.userId}, userId: ${userDetailsFromToken.getUserIdToUse()}")
        }
        deleteCommentAndAllExpandedData(request.commentId)
        return DeleteCommentResponse(request.commentId, true)
    }

    fun saveComment(request: SaveCommentRequest): Comment? {
        val userDetailsFromToken = securityProvider.validateRequest()
        return save(userDetailsFromToken.getUserIdToUse(), request)
    }

    fun updateComment(request: UpdateCommentRequest): Comment? {
        return try {
            val comment = getComment(request.commentId) ?: error("Failed to get comments data for commentId: ${request.commentId}")
            val userDetailsFromToken = securityProvider.validateRequest()
            if (comment.userId != userDetailsFromToken.getUserIdToUse()) {
                error("You are not allowed to update this comment. Only the owner can update the comment. commentId: ${request.commentId}, ownerId: ${comment.userId}, userId: ${userDetailsFromToken.getUserIdToUse()}")
            }
            val savedComment = commentRepository.save(comment.copy(
                text = request.text,
                media = request.mediaDetails?.convertToString(),
            ))
            processCommentNowAfterUpdating(savedComment)
            savedComment
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
