package com.server.ud.provider.social

import com.server.common.utils.DateUtils
import com.server.ud.dao.social.FollowingsByUserRepository
import com.server.ud.entities.social.FollowingsByUser
import com.server.ud.entities.user.UserV2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FollowingsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var followingsByUserRepository: FollowingsByUserRepository

    fun save(user: UserV2, following: UserV2) : FollowingsByUser? {
        try {
            val followingByUser = FollowingsByUser (
                userId = user.userId,
                createdAt = DateUtils.getInstantNow(),
                followingUserId = following.userId,
                userHandle = user.handle,
                followingHandle = following.handle,
                userFullName = user.fullName,
                followingFullName = following.fullName,
            )
            val savedFollowingsByUser = followingsByUserRepository.save(followingByUser)
            logger.info("Saved FollowingsByUser into cassandra for userId: ${user.userId} and followingId: ${following.userId}")
            return savedFollowingsByUser
        } catch (e: Exception) {
            logger.info("Failed: Saving FollowingsByUser into cassandra for userId: ${user.userId} and followingId: ${following.userId}")
            e.printStackTrace()
            return null
        }
    }
}
