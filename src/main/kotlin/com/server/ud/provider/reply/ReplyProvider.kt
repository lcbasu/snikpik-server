package com.server.ud.provider.reply

import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.reply.*
import com.server.ud.dto.SaveCommentReplyRequest
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.reply.RepliesByComment
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
    private lateinit var repliesByCommentRepository: RepliesByCommentRepository

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

    fun save(userId: String, request: SaveCommentReplyRequest) : Reply? {
        try {
            val reply = Reply(
                replyId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.RPL.name),
                commentId = request.commentId,
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                postId = request.postId,
                postType = request.postType,
                text = request.text,
                media = request.mediaDetails?.convertToString(),
            )
            val savedReply = commentReplyRepository.save(reply)
            postProvider.trackPost(request.postId, PostTrackerType.POST_COMMENT_REPLY, PostTrackerKeyBuilder.getPostTrackerKeyForReply(savedReply))
            processReplyNow(savedReply)
            udJobProvider.scheduleProcessingForReply(savedReply.replyId)
            return savedReply
        } catch (e: Exception) {
            e.printStackTrace()
            return null
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

    fun processReplyNow(reply: Reply) {
        runBlocking {
            logger.info("StartNow: reply processing for replyId: ${reply.replyId}")
            val repliesByCommentFuture = async { repliesByCommentProvider.save(reply) }
            val repliesCountByCommentFuture = async { repliesCountByCommentProvider.increaseRepliesCount(reply.commentId) }
            repliesByCommentFuture.await()
            repliesCountByCommentFuture.await()
            logger.info("DoneNow: reply processing for replyId: ${reply.replyId}")
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            val repliesByPost = repliesByPostProvider.getReplies(postId)
            val replyIds = repliesByPost.map { it.replyId }
            repliesByPost.map {
                val all = repliesByCommentRepository.findAllByCommentId_V2(it.commentId)
                val valid = all.filter { replyIds.contains(it.replyId) }
                repliesByCommentRepository.deleteAll(valid)
                repliesCountByCommentRepository.deleteByCommentId(it.commentId)
                replyForCommentByUserRepository.deleteByCommentIdAndUserId(it.commentId, it.userId)
            }
            replyIds.map {
                commentReplyRepository.deleteByReplyId(it)
                bookmarkProvider.deleteResourceExpandedData(it)
                likeProvider.deleteResourceExpandedData(it)
                userActivitiesProvider.deleteReplyExpandedData(it)
            }
            repliesByPostRepository.deleteAll(repliesByPost)
        }
    }
}
