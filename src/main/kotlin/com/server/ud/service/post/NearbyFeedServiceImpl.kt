package com.server.ud.service.post

import com.server.ud.dto.*
import com.server.ud.provider.post.NearbyVideoPostsByZipcodeProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NearbyFeedServiceImpl : NearbyFeedService() {

    @Autowired
    private lateinit var nearbyVideoPostsByZipcodeProvider: NearbyVideoPostsByZipcodeProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    override fun getNearbyFeed(request: NearbyFeedRequest): VideoFeedViewResultList {
        val result = nearbyVideoPostsByZipcodeProvider.getNearbyVideoFeed(request)
        return VideoFeedViewResultList(
            posts = result.content?.filterNotNull()?.map { it.toSavedPostResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getUserInfo(userId: String): VideoFeedViewSingleUserDetail {
        val user = userV2Provider.getUser(userId) ?: error("No userV2 found with id: $userId")
        return user.toVideoFeedViewSingleUserDetail()
    }
}
