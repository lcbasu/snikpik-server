package com.server.ud.provider.social

import com.google.firebase.cloud.FirestoreClient
import com.server.common.utils.CommonUtils
import com.server.ud.dao.social.SocialRelationRepository
import com.server.ud.dto.FollowersResponse
import com.server.ud.dto.GetFollowersRequest
import com.server.ud.entities.social.SocialRelation
import com.server.ud.enums.UserActivityType
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.provider.user_activity.UserActivitiesProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SocialRelationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var socialRelationRepository: SocialRelationRepository

    @Autowired
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var followersByUserProvider: FollowersByUserProvider

    @Autowired
    private lateinit var socialRelationProvider: SocialRelationProvider

    @Autowired
    private lateinit var followersCountByUserProvider: FollowersCountByUserProvider

    @Autowired
    private lateinit var followingsByUserProvider: FollowingsByUserProvider

    @Autowired
    private lateinit var followingsCountByUserProvider: FollowingsCountByUserProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var userActivitiesProvider: UserActivitiesProvider

    fun getSocialRelation(fromUserId: String, toUserId: String): SocialRelation? =
    try {
        val resourceLikes = socialRelationRepository.findAllByFromUserIdAndToUserId(fromUserId, toUserId)
        if (resourceLikes.size > 1) {
            error("More than one social relation present for fromUserId: $fromUserId & toUserId: $toUserId")
        }
        resourceLikes.firstOrNull()
    } catch (e: Exception) {
        logger.error("Getting SocialRelation for fromUserId: $fromUserId & toUserId: $toUserId failed.")
        e.printStackTrace()
        null
    }

    fun save(fromUserId: String, toUserId: String, following: Boolean = true, scheduleJob: Boolean = true) : SocialRelation? {
        try {
            val relation = SocialRelation(
                fromUserId = fromUserId,
                toUserId = toUserId,
                following = following,
            )
            val savedRelation = socialRelationRepository.save(relation)
            if (scheduleJob) {
                udJobProvider.scheduleProcessingForSocialRelation(getId(savedRelation))
            }
            saveSocialRelationToFirestore(savedRelation)
            return savedRelation
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveSocialRelationToFirestore (relation: SocialRelation) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("social_relation")
                .document(getId(relation))
                .set(relation)
        }
    }

    private fun getId(socialRelation: SocialRelation) =
        "${socialRelation.fromUserId}${CommonUtils.STRING_SEPARATOR}${socialRelation.toUserId}"

    fun getFollowers(request: GetFollowersRequest): FollowersResponse? {
        return followersByUserProvider.getFeedForFollowersResponse(request)
    }

    fun saveAllToFirestore() {
        socialRelationRepository.findAll().forEach {
            saveSocialRelationToFirestore(it!!)
        }
    }

    fun processSocialRelation(socialRelationId: String) {
        val fromUserId = socialRelationId.split(CommonUtils.STRING_SEPARATOR)[0]
        val toUserId = socialRelationId.split(CommonUtils.STRING_SEPARATOR)[1]
        processSocialRelation(fromUserId = fromUserId, toUserId = toUserId)
    }

    fun processSocialRelation(fromUserId: String, toUserId: String) {
        GlobalScope.launch {
            logger.info("Start: social relationship processing for fromUserId: $fromUserId & toUserId: $toUserId")

            val socialRelation = socialRelationProvider.getSocialRelation(
                fromUserId = fromUserId,
                toUserId = toUserId
            ) ?: error("Missing social relation for fromUserId: $fromUserId and toUserId: $toUserId")

            val fromUser = userV2Provider.getUser(fromUserId) ?: error("Error getting user for fromUserId: $fromUserId")
            val toUser = userV2Provider.getUser(toUserId) ?: error("Error getting user for toUserId: $toUserId")

            if (socialRelation.following) {
                // User just started following this user
                val userActivityFuture = async {
                    userActivitiesProvider.saveUserLevelActivity(
                        byUser = fromUser,
                        forUser = toUser,
                        userActivityType = UserActivityType.USER_FOLLOWED
                    )
                }

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
                userActivityFuture.await()

            } else {
                // User has unfollowed the other user
                val userActivityFuture = async {
                    userActivitiesProvider.deleteUserLevelActivity(
                        byUser = fromUser,
                        forUser = toUser,
                        userActivityType = UserActivityType.USER_FOLLOWED
                    )
                }

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
                userActivityFuture.await()

                // TODO: Delete the data from followersByUserProvider & followingsByUserProvider
                // Since we can not actually delete in cassandra, mark a flag in the table
                // to indicate that this data is deleted
            }
            logger.info("Done: social relationship processing for fromUserId: $fromUserId & toUserId: $toUserId")
        }
    }

}
