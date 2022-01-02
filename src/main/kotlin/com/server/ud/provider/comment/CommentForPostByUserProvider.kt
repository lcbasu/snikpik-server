package com.server.ud.provider.comment

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.comment.CommentForPostByUserRepository
import com.server.ud.entities.comment.CommentForPostByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommentForPostByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentForPostByUserRepository: CommentForPostByUserRepository

    fun getCommentForPostByUser(postId: String, userId: String): CommentForPostByUser? =
        try {
            val postLikes = commentForPostByUserRepository.findAllByPostIdAndUserId(postId, userId)
            if (postLikes.size > 1) {
                error("More than one comments has same postId: $postId by the userId: $userId")
            }
            postLikes.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting CommentForPostByUser for $postId & userId: $userId failed.")
            e.printStackTrace()
            null
        }

    fun save(postId: String, userId: String, commented: Boolean) : CommentForPostByUser? {
        return try {
            val comment = CommentForPostByUser(
                postId = postId,
                userId = userId,
                commented = commented,
            )
            val result = commentForPostByUserRepository.save(comment)
            saveCommentForPostByUserToFirestore(result)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun setCommented(postId: String, userId: String) {
        save(postId, userId, true)
    }

    private fun saveCommentForPostByUserToFirestore (commentForPostByUser: CommentForPostByUser) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("users")
                .document(commentForPostByUser.userId)
                .collection("comment_for_post_by_user")
                .document(commentForPostByUser.postId)
                .set(commentForPostByUser)
        }
    }

    fun saveAllToFirestore() {
        commentForPostByUserRepository.findAll().forEach {
            saveCommentForPostByUserToFirestore(it!!)
        }
    }

}
