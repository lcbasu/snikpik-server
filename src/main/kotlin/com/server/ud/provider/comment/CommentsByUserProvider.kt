package com.server.ud.provider.comment

import com.server.ud.dao.comment.CommentsByUserRepository
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.comment.CommentsByUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommentsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentsByUserRepository: CommentsByUserRepository

    fun getAllComments(userId: String): List<CommentsByUser> =
        try {
            commentsByUserRepository.findAllByUserId(userId)
        } catch (e: Exception) {
            logger.error("Getting CommentsByUser for $userId failed.")
            e.printStackTrace()
            emptyList()
        }

    fun save(comment: Comment) : CommentsByUser? {
        try {
            val commentsByUser = CommentsByUser(
                userId = comment.userId,
                commentId = comment.commentId,
                createdAt = comment.createdAt,
                postType = comment.postType,
                postId = comment.postId,
                text = comment.text,
                media = comment.media,
            )
            return commentsByUserRepository.save(commentsByUser)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
