package com.server.ud.service.social

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.provider.social.SocialRelationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SocialServiceImpl : SocialService() {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var socialRelationProvider: SocialRelationProvider

    override fun getRelation(otherUserId: String): SocialRelationResponse? {
        val requestContext = securityProvider.validateRequest()
        return socialRelationProvider.getSocialRelation(fromUserId = requestContext.getUserIdToUse(), toUserId = otherUserId)?.toSocialRelationResponse()
    }

    override fun setRelation(request: SocialRelationRequest): SocialRelationResponse {
        val requestContext = securityProvider.validateRequest()
        val savedRelation = socialRelationProvider.save(
            fromUserId = requestContext.getUserIdToUse(),
            toUserId = request.toUserId,
            following = request.following,
            scheduleJob = true) ?: error("Error while saving social relation for ${requestContext.getUserIdToUse()} toUser: ${request.toUserId}")
        return savedRelation.toSocialRelationResponse()
    }

    override fun getFollowers(request: GetFollowersRequest): FollowersResponse? {
        securityProvider.validateRequest()
        return socialRelationProvider.getFollowers(request)
    }
}
