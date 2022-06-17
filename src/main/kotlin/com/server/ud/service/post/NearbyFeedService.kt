package com.server.ud.service.post

import com.server.ud.dto.*

abstract class NearbyFeedService {
    abstract fun getNearbyFeed(request: NearbyFeedRequest): VideoFeedViewResultList
    abstract fun getUserInfo(userId: String): VideoFeedViewSingleUserDetail
}
