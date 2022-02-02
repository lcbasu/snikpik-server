package com.server.ud.provider.reply

import com.server.ud.dao.reply.CommentReplyRepository
import com.server.ud.dao.reply.RepliesByCommentRepository
import com.server.ud.entities.reply.Reply
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
class ReplyProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    @Autowired
    private lateinit var repliesByCommentProvider: RepliesByCommentProvider

    @Autowired
    private lateinit var repliesCountByCommentProvider: RepliesCountByCommentProvider

    @Autowired
    private lateinit var replyForCommentByUserProvider: ReplyForCommentByUserProvider

    @Autowired
    private lateinit var repliesByCommentRepository: RepliesByCommentRepository

    @Autowired
    private lateinit var commentReplyRepository: CommentReplyRepository

    @Autowired
    private lateinit var userActivitiesProvider: UserActivitiesProvider

    fun processReply(replyId: String) {
        GlobalScope.launch {
            logger.info("Start: reply processing for replyId: $replyId")
            val reply = replyProvider.getCommentReply(replyId) ?: error("Failed to get reply for replyId: $replyId")
            val replyForCommentByUserFuture = async { replyForCommentByUserProvider.setReplied(reply.commentId, reply.userId) }
            val userActivityFuture = async {
                userActivitiesProvider.saveReplyCreationActivity(reply)
            }
            replyForCommentByUserFuture.await()
            userActivityFuture.await()
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
            commentReplyRepository.deleteAll(commentReplyRepository.findAllByPostId(postId))
            repliesByCommentRepository.deleteAll(repliesByCommentRepository.findAllByPostId(postId))
        }
    }
}
