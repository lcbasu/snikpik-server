package com.server.ud.service.social

import com.server.ud.dto.FollowersResponse
import com.server.ud.dto.GetFollowersRequest
import com.server.ud.dto.SocialRelationRequest
import com.server.ud.dto.SocialRelationResponse

abstract class SocialService {
    abstract fun getRelation(otherUserId: String): SocialRelationResponse?
    abstract fun setRelation(request: SocialRelationRequest): SocialRelationResponse
    abstract fun getFollowers(request: GetFollowersRequest): FollowersResponse?
}
