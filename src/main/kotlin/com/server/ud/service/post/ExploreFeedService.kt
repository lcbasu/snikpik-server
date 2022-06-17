package com.server.ud.service.post

import com.server.ud.dto.*

abstract class ExploreFeedService {
    abstract fun getFeedForCategory(request: ExploreFeedRequest): ExploreTabViewResponse
    abstract fun getUserInfo(userId: String): ExploreTabViewUserDetail
//    abstract fun getPostLikeInfo(postId: String): ResourceLikesDetail
}
