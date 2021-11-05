package com.server.ud.service.post

import com.server.ud.dto.ExploreFeedRequest
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.pagination.CassandraPageV2

abstract class ExploreFeedService {
    abstract fun getFeedForCategory(request: ExploreFeedRequest): CassandraPageV2<PostsByCategory>
}
