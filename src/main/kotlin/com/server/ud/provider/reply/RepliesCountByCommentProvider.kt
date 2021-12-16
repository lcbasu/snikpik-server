package com.server.ud.provider.reply

import com.server.ud.dao.reply.RepliesCountByCommentRepository
import com.server.ud.entities.reply.RepliesCountByComment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RepliesCountByCommentProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var repliesCountByCommentRepository: RepliesCountByCommentRepository

    fun getRepliesCountByComment(commentId: String): RepliesCountByComment? =
        try {
            val commentReplies = repliesCountByCommentRepository.findAllByCommentId(commentId)
            if (commentReplies.size > 1) {
                error("More than one replies has same commentId: $commentId")
            }
            commentReplies.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting RepliesCountByComment for $commentId failed.")
            e.printStackTrace()
            null
        }

    fun increaseRepliesCount(commentId: String) {
        repliesCountByCommentRepository.incrementReplyCount(commentId)
        logger.warn("Increased replies count for commentId: $commentId")
    }

    fun deletePost(postId: String) {
        TODO("Add steps to delete post and related information")
    }

}
