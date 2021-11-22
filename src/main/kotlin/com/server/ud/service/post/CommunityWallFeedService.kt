package com.server.ud.service.post

import com.server.ud.dto.CommunityWallFeedRequest
import com.server.ud.dto.CommunityWallViewResponse
import com.server.ud.dto.CommunityWallViewUserDetail

abstract class CommunityWallFeedService {
    abstract fun getFeed(request: CommunityWallFeedRequest): CommunityWallViewResponse
    abstract fun getUserInfo(userId: String): CommunityWallViewUserDetail
}
