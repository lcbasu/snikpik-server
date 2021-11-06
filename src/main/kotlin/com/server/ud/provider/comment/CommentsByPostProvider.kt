package com.server.ud.provider.comment

import com.server.ud.dao.comment.CommentsByPostRepository
import com.server.ud.entities.comment.CommentsByPost
import com.server.ud.entities.comment.Comment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommentsByPostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentsByPostRepository: CommentsByPostRepository

    fun getAllComments(postId: String): List<CommentsByPost> =
        try {
            commentsByPostRepository.findAllByPostId(postId)
        } catch (e: Exception) {
            logger.error("Getting Post for $postId failed.")
            e.printStackTrace()
            emptyList()
        }

    fun save(comment: Comment) : CommentsByPost? {
        try {
            val commentsByPost = CommentsByPost(
                postId = comment.postId,
                commentId = comment.commentId,
                userId = comment.userId,
                createdAt = comment.createdAt,
                postType = comment.postType,
                text = comment.text,
                media = comment.media,
            )
            return commentsByPostRepository.save(commentsByPost)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
