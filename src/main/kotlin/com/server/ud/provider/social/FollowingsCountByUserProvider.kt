package com.server.ud.provider.social

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.social.FollowingsCountByUserRepository
import com.server.ud.entities.social.FollowingsCountByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FollowingsCountByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var followingsCountByUserRepository: FollowingsCountByUserRepository

    fun getFollowingsCountByUser(userId: String): FollowingsCountByUser? =
        try {
            val followingsCount = followingsCountByUserRepository.findAllByUserId(userId)
            if (followingsCount.size > 1) {
                error("More than one followings count has same userId: $userId")
            }
            followingsCount.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting followings count for $userId failed.")
            e.printStackTrace()
            null
        }

    fun increaseFollowingsCount(userId: String) {
        followingsCountByUserRepository.incrementFollowings(userId)
        logger.warn("Increased followings count for userId: $userId")
        saveFollowingsCountByUserToFirestore(getFollowingsCountByUser(userId))
    }

    fun decreaseFollowingsCount(userId: String) {
        val existing = getFollowingsCountByUser(userId)
        if (existing?.followingsCount != null && existing.followingsCount!! > 0) {
            followingsCountByUserRepository.decrementFollowings(userId)
            logger.warn("Decreased followings count for userId: $userId")
        } else {
            logger.warn("The followings count is already zero. So skipping decreasing it further for userId: $userId")
        }
        saveFollowingsCountByUserToFirestore(getFollowingsCountByUser(userId))
    }

    private fun saveFollowingsCountByUserToFirestore (followingsCountByUser: FollowingsCountByUser?) {
        GlobalScope.launch {
            if (followingsCountByUser?.userId == null) {
                logger.error("No userId found in followings count by user. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("users")
                .document(followingsCountByUser.userId!!)
                .collection("followings_count_by_user")
                .document(followingsCountByUser.userId!!)
                .set(followingsCountByUser)
        }
    }

    fun saveAllToFirestore() {
        followingsCountByUserRepository.findAll().forEach {
            saveFollowingsCountByUserToFirestore(it)
        }
    }
}
