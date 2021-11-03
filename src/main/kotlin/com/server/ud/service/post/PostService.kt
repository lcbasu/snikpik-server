package com.server.ud.service.post

import com.server.ud.dto.FakePostRequest
import com.server.ud.dto.PaginatedRequest
import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.SavedPostResponse
import com.server.ud.entities.post.Post
import com.server.ud.pagination.CassandraPageV2

abstract class PostService {
    abstract fun savePost(savePostRequest: SavePostRequest): SavedPostResponse?
    abstract fun fakeSavePosts(request: FakePostRequest): List<SavedPostResponse>?
    abstract fun getPosts(request: PaginatedRequest): CassandraPageV2<Post?>?
}
