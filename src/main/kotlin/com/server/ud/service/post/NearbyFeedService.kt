package com.server.ud.service.post

import com.server.ud.dto.*
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.pagination.CassandraPageV2

abstract class NearbyFeedService {
    abstract fun getNearbyFeed(request: NearbyFeedRequest): VideoFeedViewResultList
    abstract fun getUserInfo(userId: String): VideoFeedViewSingleUserDetail
}
