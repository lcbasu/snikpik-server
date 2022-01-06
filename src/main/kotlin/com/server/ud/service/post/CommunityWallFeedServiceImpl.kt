package com.server.ud.service.post

import com.server.ud.dto.*
import com.server.ud.provider.post.NearbyPostsByZipcodeProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommunityWallFeedServiceImpl : CommunityWallFeedService() {

    @Autowired
    private lateinit var nearbyPostsByZipcodeProvider: NearbyPostsByZipcodeProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    override fun getFeed(request: CommunityWallFeedRequest): CommunityWallViewResponse {
        val result = nearbyPostsByZipcodeProvider.getCommunityWallFeed(request)
        return CommunityWallViewResponse(
            posts = result.content?.filterNotNull()?.map { it.toSavedPostResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getUserInfo(userId: String): CommunityWallViewUserDetail {
        val user = userV2Provider.getUser(userId) ?: error("No userV2 found with id: $userId")
        return user.toCommunityWallViewUserDetail()
    }

}
