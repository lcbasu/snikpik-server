package com.server.ud.service.post

import com.server.ud.dto.ExploreFeedRequest
import com.server.ud.dto.ExploreTabViewLikesDetail
import com.server.ud.dto.ExploreTabViewResponse
import com.server.ud.dto.ExploreTabViewUserDetail
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.pagination.CassandraPageV2

abstract class ExploreFeedService {
    abstract fun getFeedForCategory(request: ExploreFeedRequest): ExploreTabViewResponse
    abstract fun getUserInfo(userId: String): ExploreTabViewUserDetail
    abstract fun getPostLikeInfo(postId: String): ExploreTabViewLikesDetail
}
