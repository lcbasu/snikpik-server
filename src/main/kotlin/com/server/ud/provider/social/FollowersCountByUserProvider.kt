package com.server.ud.provider.social

import com.server.ud.dao.social.FollowersCountByUserRepository
import com.server.ud.entities.social.FollowersCountByUser
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
            followersCount.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting followers count for $userId failed.")
            e.printStackTrace()
            null
        }

    fun increaseFollowersCount(userId: String) {
        followersCountByUserRepository.incrementFollowers(userId)
        logger.warn("Increased followers count for userId: $userId")
    }

    fun decreaseFollowersCount(userId: String) {
        val existing = getFollowersCountByUser(userId)
        if (existing?.followersCount != null && existing.followersCount!! > 0) {
            followersCountByUserRepository.decrementFollowers(userId)
            logger.warn("Decreased followers count for userId: $userId")
        } else {
            logger.warn("The followers count is already zero. So skipping decreasing it further for userId: $userId")
        }
    }
}
