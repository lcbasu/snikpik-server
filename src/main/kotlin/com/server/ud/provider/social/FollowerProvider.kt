package com.server.ud.provider.social

import com.server.ud.dao.social.FollowerRepository
import com.server.ud.entities.social.Follower
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FollowerProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var followerRepository: FollowerRepository

    // TODO: Optimize this ASAP
    fun getFollowers(userId: String): List<Follower>? =
        try {
            followerRepository.findAllFollowersForUserId(userId)
        } catch (e: Exception) {
            logger.error("Getting Followers for $userId failed.")
            e.printStackTrace()
            null
        }


}
