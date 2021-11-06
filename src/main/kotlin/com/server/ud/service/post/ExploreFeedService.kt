package com.server.ud.service.post

import com.server.ud.dto.*
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.pagination.CassandraPageV2

abstract class ExploreFeedService {
    abstract fun getFeedForCategory(request: ExploreFeedRequest): ExploreTabViewResponse
    abstract fun getUserInfo(userId: String): ExploreTabViewUserDetail
//    abstract fun getPostLikeInfo(postId: String): ResourceLikesDetail
}
