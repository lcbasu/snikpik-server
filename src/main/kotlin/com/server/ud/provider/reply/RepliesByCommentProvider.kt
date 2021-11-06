package com.server.ud.provider.reply

import com.server.ud.dao.reply.RepliesByCommentRepository
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.reply.RepliesByComment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RepliesByCommentProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var repliesByCommentRepository: RepliesByCommentRepository

    fun getAllReplies(commentId: String): List<RepliesByComment> =
        try {
            repliesByCommentRepository.findAllByCommentId(commentId)
        } catch (e: Exception) {
            logger.error("Getting RepliesByComment for $commentId failed.")
            e.printStackTrace()
            emptyList()
        }

    fun save(reply: Reply) : RepliesByComment? {
        try {
            val repliesByComment = RepliesByComment(
                commentId = reply.commentId,
                createdAt = reply.createdAt,
                replyId = reply.replyId,
                userId = reply.userId,
                replyText = reply.text,
                media = reply.media,
            )
            return repliesByCommentRepository.save(repliesByComment)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
