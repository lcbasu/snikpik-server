package com.server.ud.provider.like

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.like.LikesCountByUserRepository
import com.server.ud.entities.like.LikesCountByUser
import com.server.ud.entities.user.PostsCountByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LikesCountByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likesCountByUserRepository: LikesCountByUserRepository

    fun getLikesCountByUser(userId: String): LikesCountByUser? =
        try {
            val resourceLikes = likesCountByUserRepository.findAllByUserId(userId)
            if (resourceLikes.size > 1) {
                error("More than one likes has same userId: $userId")
            }
            resourceLikes.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting LikesCountByUser for $userId failed.")
            e.printStackTrace()
            null
        }

    fun increaseLike(userId: String) {
        likesCountByUserRepository.incrementLikes(userId)
        logger.warn("Increased like for userId: $userId")
        saveLikesCountByUserToFirestore(getLikesCountByUser(userId))
    }

    fun decreaseLike(userId: String) {
        val existing = getLikesCountByUser(userId)
        if (existing?.likesCount != null && existing.likesCount!! > 0) {
            likesCountByUserRepository.decrementLikes(userId)
            logger.warn("Decreased like for userId: $userId")
        } else {
            logger.warn("The likes count is already zero. So skipping decreasing it further for userId: $userId")
        }
        saveLikesCountByUserToFirestore(getLikesCountByUser(userId))
    }

    fun deletePost(postId: String) {
        TODO("Add steps to delete post and related information")
    }

    private fun saveLikesCountByUserToFirestore (likesCountByUser: LikesCountByUser?) {
        GlobalScope.launch {
            if (likesCountByUser?.userId == null) {
                logger.error("No user id found in likesCountByUser. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("users")
                .document(likesCountByUser.userId!!)
                .collection("likes_count_by_user")
                .document(likesCountByUser.userId!!)
                .set(likesCountByUser)
        }
    }

    fun saveAllToFirestore() {
        likesCountByUserRepository.findAll().forEach {
            saveLikesCountByUserToFirestore(it)
        }
    }
}
