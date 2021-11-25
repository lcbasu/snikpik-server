package com.server.ud.provider.reply

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

    fun processReply(replyId: String) {
        GlobalScope.launch {
            logger.info("Start: reply processing for replyId: $replyId")
            val reply = replyProvider.getCommentReply(replyId) ?: error("Failed to get reply for replyId: $replyId")
            val repliesByCommentFuture = async { repliesByCommentProvider.save(reply) }
            val repliesCountByCommentFuture = async { repliesCountByCommentProvider.increaseRepliesCount(reply.commentId) }
            val replyForCommentByUserFuture = async { replyForCommentByUserProvider.setReplied(reply.commentId, reply.userId) }
            repliesByCommentFuture.await()
            repliesCountByCommentFuture.await()
            replyForCommentByUserFuture.await()
            logger.info("Done: reply processing for replyId: $replyId")
        }
    }
}
