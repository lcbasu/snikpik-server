package com.server.ud.provider.reply

import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.reply.CommentReplyRepository
import com.server.ud.dao.reply.RepliesByPostRepository
import com.server.ud.dao.reply.RepliesCountByCommentRepository
import com.server.ud.dao.reply.ReplyForCommentByUserRepository
import com.server.ud.dto.*
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.reply.Reply
import com.server.ud.enums.PostTrackerType
import com.server.ud.provider.bookmark.BookmarkProvider
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.user_activity.UserActivitiesProvider
import com.server.ud.utils.PostTrackerKeyBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReplyProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentReplyRepository: CommentReplyRepository

    @Autowired
    private lateinit var bookmarkProvider: BookmarkProvider

    @Autowired
    private lateinit var likeProvider: LikeProvider

    @Autowired
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var repliesByCommentProvider: RepliesByCommentProvider

    @Autowired
    private lateinit var repliesCountByCommentProvider: RepliesCountByCommentProvider

    @Autowired
    private lateinit var replyForCommentByUserProvider: ReplyForCommentByUserProvider

    @Autowired
    private lateinit var userActivitiesProvider: UserActivitiesProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var repliesByPostProvider: RepliesByPostProvider

    @Autowired
    private lateinit var repliesByPostRepository: RepliesByPostRepository

    @Autowired
    private lateinit var repliesCountByCommentRepository: RepliesCountByCommentRepository

    @Autowired
    private lateinit var replyForCommentByUserRepository: ReplyForCommentByUserRepository

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun getCommentReply(replyId: String): Reply? =
        try {
            val replies = commentReplyRepository.findAllByReplyId(replyId)
            if (replies.size > 1) {
                error("More than one reply has same replyId: $replyId")
            }
            replies.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting CommentReply for $replyId failed.")
            e.printStackTrace()
            null
        }

    fun saveReply(request: SaveCommentReplyRequest): SavedCommentReplyResponse? {
        val userDetailsFromToken = securityProvider.validateRequest()
        return save(userDetailsFromToken.getUserIdToUse(), request)?.toSavedCommentReplyResponse()
    }

    fun save(userId: String, request: SaveCommentReplyRequest) : Reply? {
        try {
            val reply = Reply(
                replyId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.RPL.name),
                commentId = request.commentId,
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                postId = request.postId,
                postType = request.postType,
                text = request.text,
                media = request.mediaDetails?.convertToString(),
            )
            val savedReply = commentReplyRepository.save(reply)
            processReplyNowAfterSavingFirstTime(savedReply)
            return savedReply
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun updateReply(request: UpdateCommentReplyRequest): Reply? {
        return try {
            val reply = getCommentReply(request.replyId) ?: error("Reply not found for replyId: ${request.replyId}")
            val userDetailsFromToken = securityProvider.validateRequest()
            if (reply.userId != userDetailsFromToken.getUserIdToUse()) {
                error("You are not allowed to update this reply. Only the owner can update the reply. replyId: ${request.replyId}, ownerId: ${reply.userId}, userId: ${userDetailsFromToken.getUserIdToUse()}")
            }
            val savedReply = commentReplyRepository.save(reply.copy(
                text = request.text,
                media = request.mediaDetails?.convertToString()
            ))
            processReplyNowAfterUpdating(savedReply)
            savedReply
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun handleProcessedMedia(updatedMediaDetail: MediaProcessingDetail) {
        TODO("Not yet implemented")
    }

    fun processReply(replyId: String) {
        GlobalScope.launch {
            logger.info("Start: reply processing for replyId: $replyId")
            val reply = getCommentReply(replyId) ?: error("Failed to get reply for replyId: $replyId")
            val replyForCommentByUserFuture = async { replyForCommentByUserProvider.setReplied(reply.commentId, reply.userId) }
            val userActivityFuture = async {
                userActivitiesProvider.saveReplyCreationActivity(reply)
            }
            val repliesByPostFuture = async { repliesByPostProvider.save(reply) }
            replyForCommentByUserFuture.await()
            userActivityFuture.await()
            repliesByPostFuture.await()
            logger.info("Done: reply processing for replyId: $replyId")
        }
    }

    fun processReplyNowAfterSavingFirstTime(reply: Reply) {
        runBlocking {
            logger.info("StartNow: reply processing for replyId: ${reply.replyId}")
            postProvider.trackPost(reply.postId, PostTrackerType.POST_COMMENT_REPLY, PostTrackerKeyBuilder.getPostTrackerKeyForReply(reply))
            val repliesByCommentFuture = async { repliesByCommentProvider.save(reply) }
            val repliesCountByCommentFuture = async { repliesCountByCommentProvider.increaseRepliesCount(reply.commentId) }
            repliesByCommentFuture.await()
            repliesCountByCommentFuture.await()
            udJobProvider.scheduleProcessingForReply(reply.replyId)
            logger.info("DoneNow: reply processing for replyId: ${reply.replyId}")
        }
    }

    fun processReplyNowAfterUpdating(reply: Reply) {
        runBlocking {
            logger.info("StartNow: reply processing for update for replyId: ${reply.replyId}")
            postProvider.trackPost(reply.postId, PostTrackerType.POST_COMMENT_REPLY, PostTrackerKeyBuilder.getPostTrackerKeyForReply(reply))
            udJobProvider.scheduleProcessingForReply(reply.replyId)
            repliesByCommentProvider.save(reply)
            logger.info("DoneNow: reply processing for update for replyId: ${reply.replyId}")
        }
    }

//    fun deleteAllRepliesOfPost(postId: String) {
//        GlobalScope.launch {
//            val repliesByPost = repliesByPostProvider.getReplies(postId)
//            repliesByPost.map {
//                deleteReplyAndAllExpandedData(it.replyId)
//            }
//
//        }
//    }

    fun deleteAllRepliesOfComment(commentId: String) {
        GlobalScope.launch {
            val allRepliesForThisCommentByAllUsers = repliesByCommentProvider.getAllRepliesByComment(commentId)
            allRepliesForThisCommentByAllUsers.map {
                deleteReplyAndAllExpandedData(it.replyId)
            }

        }
    }

    private fun deleteReplyAndAllExpandedData(replyId: String) {
        GlobalScope.launch {
            val reply = getCommentReply(replyId) ?: error("Failed to get reply for replyId: $replyId")
            deleteReplyAndAllExpandedData(reply)
        }
    }

    private fun deleteReplyAndAllExpandedData(reply: Reply) {
        GlobalScope.launch {

            bookmarkProvider.deleteResourceExpandedData(reply.replyId)
            likeProvider.deleteResourceExpandedData(reply.replyId)
            userActivitiesProvider.deleteReplyExpandedData(reply.replyId)

            val allRepliesForThisCommentByAllUsers = repliesByCommentProvider.getAllRepliesByComment(reply.commentId)
            val allRepliesForThisCommentByThisUser = allRepliesForThisCommentByAllUsers.filter { it.userId == reply.userId }

            if (allRepliesForThisCommentByThisUser.isEmpty() || allRepliesForThisCommentByThisUser.size == 1 && allRepliesForThisCommentByThisUser.first().replyId == reply.replyId) {
                replyForCommentByUserProvider.resetReplied(reply.commentId, reply.userId)
            } else {
                replyForCommentByUserProvider.setReplied(reply.commentId, reply.userId)
            }

            repliesByCommentProvider.deleteAllRepliesByComment(reply)

            repliesByPostProvider.deleteAllRepliesByPost(reply)

            repliesCountByCommentProvider.decreaseRepliesCount(reply.commentId)

            delete(reply.replyId)
        }
    }

    fun delete(replyId: String) {
        try {
            commentReplyRepository.deleteByReplyId(replyId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteReply(request: DeleteCommentReplyRequest): DeletedCommentReplyResponse? {
        val userDetailsFromToken = securityProvider.validateRequest()
        val reply = getCommentReply(request.replyId) ?: error("Failed to get reply for replyId: ${request.replyId}")
        if (reply.userId != userDetailsFromToken.getUserIdToUse()) {
            error("You are not allowed to delete this reply. Only the owner can delete the reply. replyId: ${request.replyId}, ownerId: ${reply.userId}, userId: ${userDetailsFromToken.getUserIdToUse()}")
        }
        deleteReplyAndAllExpandedData(request.replyId)
        return DeletedCommentReplyResponse(request.replyId, true)
    }
}
