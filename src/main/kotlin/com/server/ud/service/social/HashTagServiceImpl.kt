package com.server.ud.service.social

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.SaveHashTagsRequest
import com.server.ud.dto.SavedHashTagsResponse
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.post.PostsCountByUserProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class HashTagServiceImpl : HashTagService() {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var postsCountByUserProvider: PostsCountByUserProvider
    override fun saveHashTags(request: SaveHashTagsRequest): SavedHashTagsResponse? {
        TODO("Not yet implemented")
    }

}
