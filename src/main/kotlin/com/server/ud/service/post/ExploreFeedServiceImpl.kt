package com.server.ud.service.post

import com.server.ud.dto.ExploreFeedRequest
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.post.PostsByCategoryProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExploreFeedServiceImpl : ExploreFeedService() {

    @Autowired
    private lateinit var postsByCategoryProvider: PostsByCategoryProvider

    override fun getFeedForCategory(request: ExploreFeedRequest): CassandraPageV2<PostsByCategory> {
        return postsByCategoryProvider.getFeedForCategory(request)
    }
}
