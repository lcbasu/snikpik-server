package com.server.ud.provider.social

import com.server.common.utils.CommonUtils
import com.server.ud.dao.social.SocialRelationRepository
import com.server.ud.dto.FollowersResponse
import com.server.ud.dto.GetFollowersRequest
import com.server.ud.entities.social.SocialRelation
import com.server.ud.provider.deferred.DeferredProcessingProvider
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
    private lateinit var deferredProcessingProvider: DeferredProcessingProvider

    @Autowired
    private lateinit var followersByUserProvider: FollowersByUserProvider

    fun getSocialRelation(fromUserId: String, toUserId: String): SocialRelation? =
    try {
        val resourceLikes = socialRelationRepository.getAllByUserAndOtherUser(fromUserId, toUserId)
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
                deferredProcessingProvider.deferProcessingForSocialRelation(getId(savedRelation))
            }
            return savedRelation
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getId(socialRelation: SocialRelation) =
        "${socialRelation.fromUserId}${CommonUtils.STRING_SEPARATOR}${socialRelation.toUserId}"

    fun getFollowers(request: GetFollowersRequest): FollowersResponse? {
        return followersByUserProvider.getFeedForFollowersResponse(request)
    }

}
