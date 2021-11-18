package com.server.ud.service.social

import com.server.common.provider.AuthProvider
import com.server.ud.dto.*
import com.server.ud.provider.social.SocialRelationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SocialServiceImpl : SocialService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var socialRelationProvider: SocialRelationProvider

    override fun getRelation(otherUserId: String): SocialRelationResponse? {
        val requestContext = authProvider.validateRequest()
        return socialRelationProvider.getSocialRelation(fromUserId = requestContext.userV2.userId, toUserId = otherUserId)?.toSocialRelationResponse()
    }

    override fun setRelation(request: SocialRelationRequest): SocialRelationResponse {
        val requestContext = authProvider.validateRequest()
        val savedRelation = socialRelationProvider.save(
            fromUserId = requestContext.userV2.userId,
            toUserId = request.toUserId,
            following = request.following,
            scheduleJob = true) ?: error("Error while saving social relation for ${requestContext.userV2.userId} toUser: ${request.toUserId}")
        return savedRelation.toSocialRelationResponse()
    }

    override fun getFollowers(request: GetFollowersRequest): FollowersResponse? {
        val requestContext = authProvider.validateRequest()
        return socialRelationProvider.getFollowers(request)
    }
}
