package com.server.ud.service.post

import com.server.ud.dto.ExploreFeedRequest
import com.server.ud.dto.ExploreTabViewResponse
import com.server.ud.dto.ExploreTabViewUserDetail
import com.server.ud.dto.toExploreTabViewUserDetail
import com.server.ud.provider.post.PostsByCategoryProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExploreFeedServiceImpl : ExploreFeedService() {

    @Autowired
    private lateinit var postsByCategoryProvider: PostsByCategoryProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    override fun getFeedForCategory(request: ExploreFeedRequest): ExploreTabViewResponse {
        return postsByCategoryProvider.getExploreTabViewResponse(request)
    }

    override fun getUserInfo(userId: String): ExploreTabViewUserDetail {
        val user = userV2Provider.getUser(userId) ?: error("No userV2 found with id: $userId")
        return user.toExploreTabViewUserDetail()
    }
}
