package com.server.ud.provider.social

import com.server.common.utils.DateUtils
import com.server.ud.dao.social.FollowersByUserRepository
import com.server.ud.entities.social.FollowersByUser
import com.server.ud.entities.user.UserV2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FollowersByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var followersByUserRepository: FollowersByUserRepository

    // TODO: Optimize this ASAP
    fun getFollowers(userId: String): List<FollowersByUser>? =
        emptyList()
//        try {
//            followersByUserRepository.findAllFollowersForUserId(userId)
//        } catch (e: Exception) {
//            logger.error("Getting Followers for $userId failed.")
//            e.printStackTrace()
//            null
//        }

    fun save(user: UserV2, follower: UserV2) : FollowersByUser? {
        try {
            val followersByUser = FollowersByUser (
                userId = user.userId,
                forDate = DateUtils.getInstantToday(),
                createdAt = DateUtils.getInstantNow(),
                followerUserId = follower.userId,
                userHandle = user.handle,
                followerHandle = follower.handle,
                userFullName = user.fullName,
                followerFullName = follower.fullName,
            )
            val savedFollowersByUser = followersByUserRepository.save(followersByUser)
            logger.info("Saved FollowersByUser into cassandra for userId: ${user.userId} and followerId: ${follower.userId}")
            return savedFollowersByUser
        } catch (e: Exception) {
            logger.info("Failed: Saving FollowersByUser into cassandra for userId: ${user.userId} and followerId: ${follower.userId}")
            e.printStackTrace()
            return null
        }
    }
}
