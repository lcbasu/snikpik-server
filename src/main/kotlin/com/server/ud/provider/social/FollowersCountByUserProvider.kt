package com.server.ud.provider.social

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.social.FollowersCountByUserRepository
import com.server.ud.entities.social.FollowersCountByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FollowersCountByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var followersCountByUserRepository: FollowersCountByUserRepository

    fun getFollowersCountByUser(userId: String): FollowersCountByUser? =
        try {
            val followersCount = followersCountByUserRepository.findAllByUserId(userId)
            if (followersCount.size > 1) {
                error("More than one followers count has same userId: $userId")
            }
            followersCount.getOrElse(0) {
                val followersCountByUser = FollowersCountByUser()
                followersCountByUser.followersCount = 0
                followersCountByUser.userId = userId
                followersCountByUser
            }
        } catch (e: Exception) {
            logger.error("Getting followers count for $userId failed.")
            e.printStackTrace()
            null
        }

    fun increaseFollowersCount(userId: String) {
        followersCountByUserRepository.incrementFollowers(userId)
        logger.warn("Increased followers count for userId: $userId")
        saveFollowersCountByUserToFirestore(getFollowersCountByUser(userId))
    }

    fun decreaseFollowersCount(userId: String) {
        val existing = getFollowersCountByUser(userId)
        if (existing?.followersCount != null && existing.followersCount!! > 0) {
            followersCountByUserRepository.decrementFollowers(userId)
            logger.warn("Decreased followers count for userId: $userId")
        } else {
            logger.warn("The followers count is already zero. So skipping decreasing it further for userId: $userId")
        }
        saveFollowersCountByUserToFirestore(getFollowersCountByUser(userId))
    }

    private fun saveFollowersCountByUserToFirestore (followersCountByUser: FollowersCountByUser?) {
        GlobalScope.launch {
            if (followersCountByUser?.userId == null) {
                logger.error("No userId found in followers count by user. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("users")
                .document(followersCountByUser.userId!!)
                .collection("followers_count_by_user")
                .document(followersCountByUser.userId!!)
                .set(followersCountByUser)
        }
    }

    fun saveAllToFirestore() {
        followersCountByUserRepository.findAll().forEach {
            saveFollowersCountByUserToFirestore(it)
        }
    }
}
