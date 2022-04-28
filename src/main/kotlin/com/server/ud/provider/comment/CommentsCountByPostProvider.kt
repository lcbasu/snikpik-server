package com.server.ud.provider.comment

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.comment.CommentsCountByPostRepository
import com.server.ud.entities.comment.CommentsCountByPost
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommentsCountByPostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentsCountByPostRepository: CommentsCountByPostRepository

    fun getCommentsCountByPost(postId: String): CommentsCountByPost? =
        try {
            val postComments = commentsCountByPostRepository.findAllByPostId(postId)
            if (postComments.size > 1) {
                error("More than one comments has same postId: $postId")
            }
            postComments.getOrElse(0) {
                val commentsCountByPost = CommentsCountByPost()
                commentsCountByPost.commentsCount = 0
                commentsCountByPost.postId = postId
                commentsCountByPost
            }
        } catch (e: Exception) {
            logger.error("Getting CommentsCountByPost for $postId failed.")
            e.printStackTrace()
            null
        }

    fun increaseCommentCount(postId: String) {
        commentsCountByPostRepository.incrementCommentCount(postId)
        logger.warn("Increased comment for postId: $postId")
        saveCommentsCountByPostToFirestore(getCommentsCountByPost(postId))
    }

    fun decreaseCommentCount(postId: String) {
        commentsCountByPostRepository.decrementCommentCount(postId)
        logger.warn("Decreased comment for postId: $postId")
        saveCommentsCountByPostToFirestore(getCommentsCountByPost(postId))
    }

    // Decreasing is Not supported as the comments are immutable right now
    // without an ability to delete the comments
//    fun decreaseCommentCount(postId: String) {
//        val existing = getCommentsCountByPost(postId)
//        if (existing?.commentsCount != null && existing.commentsCount!! > 0) {
//            commentsCountByPostRepository.decrementCommentCount(postId)
//            logger.warn("Decreased comment for postId: $postId")
//        } else {
//            logger.warn("The comments count is already zero. So skipping decreasing it further for postId: $postId")
//        }
//    }
//
//    fun resetCommentsCount(postId: String) =
//        commentsCountByPostRepository.setCommentCount(postId, 0)

    private fun saveCommentsCountByPostToFirestore (commentsCountByPost: CommentsCountByPost?) {
        GlobalScope.launch {
            if (commentsCountByPost?.postId == null) {
                logger.error("No post id found in commentsCountByPost. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("comments_count_by_post")
                .document(commentsCountByPost.postId!!)
                .set(commentsCountByPost)
        }
    }

    fun saveAllToFirestore() {
        commentsCountByPostRepository.findAll().forEach {
            saveCommentsCountByPostToFirestore(it)
        }
    }

}
