package com.server.ud.provider.reply

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.reply.RepliesCountByCommentRepository
import com.server.ud.entities.reply.RepliesCountByComment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            commentReplies.getOrElse(0) {
                val repliesCountByComment = RepliesCountByComment()
                repliesCountByComment.repliesCount = 0
                repliesCountByComment.commentId = commentId
                repliesCountByComment
            }
        } catch (e: Exception) {
            logger.error("Getting RepliesCountByComment for $commentId failed.")
            e.printStackTrace()
            null
        }

    fun increaseRepliesCount(commentId: String) {
        repliesCountByCommentRepository.incrementReplyCount(commentId)
        logger.warn("Increased replies count for commentId: $commentId")
        saveRepliesCountByCommentToFirestore(getRepliesCountByComment(commentId))
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }

    private fun saveRepliesCountByCommentToFirestore (repliesCountByComment: RepliesCountByComment?) {
        GlobalScope.launch {
            if (repliesCountByComment?.commentId == null) {
                logger.error("No comment found in repliesCountByComment. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("replies_count_by_comment")
                .document(repliesCountByComment.commentId!!)
                .set(repliesCountByComment)
        }
    }

    fun saveAllToFirestore() {
        repliesCountByCommentRepository.findAll().forEach {
            saveRepliesCountByCommentToFirestore(it)
        }
    }

}
