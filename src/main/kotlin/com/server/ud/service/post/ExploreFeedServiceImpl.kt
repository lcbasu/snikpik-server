package com.server.ud.service.post

import com.server.common.provider.AuthProvider
import com.server.ud.dto.*
import com.server.ud.provider.like.LikesCountByResourceAndUserProvider
import com.server.ud.provider.like.LikesCountByResourceProvider
import com.server.ud.provider.post.PostsByCategoryProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExploreFeedServiceImpl : ExploreFeedService() {

    @Autowired
    private lateinit var postsByCategoryProvider: PostsByCategoryProvider

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var likesCountByResourceProvider: LikesCountByResourceProvider

    @Autowired
    private lateinit var likesCountByResourceAndUserProvider: LikesCountByResourceAndUserProvider

    override fun getFeedForCategory(request: ExploreFeedRequest): ExploreTabViewResponse {
        val result = postsByCategoryProvider.getFeedForCategory(request)
        return ExploreTabViewResponse(
            posts = result.content?.filterNotNull()?.map { it.toExploreTabViewPostDetail() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getUserInfo(userId: String): ExploreTabViewUserDetail {
        val user = userV2Provider.getUser(userId) ?: error("No userV2 found with id: $userId")
        return user.toExploreTabViewUserDetail()
    }

    override fun getPostLikeInfo(postId: String): ExploreTabViewLikesDetail {
        val requestContext = authProvider.validateRequest()
        val likesCountByResource = likesCountByResourceProvider.getLikesCountByResource(postId)?.likesCount ?: 0
        val likesCountByUser = likesCountByResourceAndUserProvider.getLikesCountByResourceAndUser(
            resourceId = postId,
            userId = requestContext.userV2.userId)?.likesCount ?: 0
        return ExploreTabViewLikesDetail(
            postId = postId,
            likes = likesCountByResource,
            liked = likesCountByUser == 1L
        )
    }
}
