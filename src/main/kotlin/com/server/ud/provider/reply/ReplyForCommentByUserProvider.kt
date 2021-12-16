package com.server.ud.provider.reply

import com.server.ud.dao.reply.ReplyForCommentByUserRepository
import com.server.ud.entities.reply.ReplyForCommentByUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReplyForCommentByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var replyForCommentByUserRepository: ReplyForCommentByUserRepository

    fun getRepliesForCommentByUser(commentId: String, userId: String): ReplyForCommentByUser? =
        try {
            val commentReplies = replyForCommentByUserRepository.findAllByCommentIdAndUserId(commentId, userId)
            if (commentReplies.size > 1) {
                error("More than one replies has same commentId: $commentId by the userId: $userId")
            }
            commentReplies.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting ReplyForCommentByUser for $commentId & userId: $userId failed.")
            e.printStackTrace()
            null
        }

    fun save(commentId: String, userId: String, replied: Boolean) : ReplyForCommentByUser? {
        try {
            val comment = ReplyForCommentByUser(
                commentId = commentId,
                userId = userId,
                replied = replied,
            )
            return replyForCommentByUserRepository.save(comment)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun setReplied(commentId: String, userId: String) {
        save(commentId, userId, true)
    }

    fun deletePost(postId: String) {
        TODO("Add steps to delete post and related information")
    }

}
