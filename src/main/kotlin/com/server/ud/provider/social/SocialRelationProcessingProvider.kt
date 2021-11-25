package com.server.ud.provider.social

import com.server.common.utils.CommonUtils
import com.server.ud.provider.user.UserV2Provider
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SocialRelationProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var socialRelationProvider: SocialRelationProvider

    @Autowired
    private lateinit var followersByUserProvider: FollowersByUserProvider

    @Autowired
    private lateinit var followersCountByUserProvider: FollowersCountByUserProvider

    @Autowired
    private lateinit var followingsByUserProvider: FollowingsByUserProvider

    @Autowired
    private lateinit var followingsCountByUserProvider: FollowingsCountByUserProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    fun processSocialRelation(socialRelationId: String) {
        val fromUserId = socialRelationId.split(CommonUtils.STRING_SEPARATOR)[0]
        val toUserId = socialRelationId.split(CommonUtils.STRING_SEPARATOR)[1]
        processSocialRelation(fromUserId = fromUserId, toUserId = toUserId)
    }

    fun processSocialRelation(fromUserId: String, toUserId: String) {
        runBlocking {
            logger.info("Do social relationship processing for fromUserId: $fromUserId & toUserId: $toUserId")

            val socialRelation = socialRelationProvider.getSocialRelation(
                fromUserId = fromUserId,
                toUserId = toUserId
            ) ?: error("Missing social relation for fromUserId: $fromUserId and toUserId: $toUserId")

            val fromUser = userV2Provider.getUser(fromUserId) ?: error("Error getting user for fromUserId: $fromUserId")
            val toUser = userV2Provider.getUser(toUserId) ?: error("Error getting user for toUserId: $toUserId")

            if (socialRelation.following) {
                // User just started following this user

                // Update followers count
                val followersCountByUserFuture = async {
                    followersCountByUserProvider.increaseFollowersCount(toUserId)
                }

                // Update followings count
                val followingsCountByUserFuture = async {
                    followingsCountByUserProvider.increaseFollowingsCount(fromUserId)
                }

                // Update followers
                val followersByUserFuture = async {
                    followersByUserProvider.save(user = toUser, follower = fromUser)
                }

                // Update followings
                val followingsByUserFuture = async {
                    followingsByUserProvider.save(user = fromUser, following = toUser)
                }

                followersCountByUserFuture.await()
                followingsCountByUserFuture.await()
                followersByUserFuture.await()
                followingsByUserFuture.await()

            } else {
                // User has unfollowed the other user
                // Update followers count
                val followersCountByUserFuture = async {
                    followersCountByUserProvider.decreaseFollowersCount(toUserId)
                }

                // Update followings count
                val followingsCountByUserFuture = async {
                    followingsCountByUserProvider.decreaseFollowingsCount(fromUserId)
                }

                followersCountByUserFuture.await()
                followingsCountByUserFuture.await()

                // TODO: Delete the data from followersByUserProvider & followingsByUserProvider
                // Since we can not actually delete in cassandra, mark a flag in the table
                // to indicate that this data is deleted
            }
        }
    }


}
