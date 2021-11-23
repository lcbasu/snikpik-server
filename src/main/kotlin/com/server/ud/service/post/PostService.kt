package com.server.ud.service.post

import com.server.ud.dto.*
import com.server.ud.entities.post.Post
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.pagination.CassandraPageV2

abstract class PostService {
    abstract fun savePost(savePostRequest: SavePostRequest): SavedPostResponse?
    abstract fun fakeSavePosts(request: FakePostRequest): List<SavedPostResponse>?
    abstract fun getPosts(request: PaginatedRequest): CassandraPageV2<Post?>?
    abstract fun getPost(postId: String): Post?
    abstract fun getPostsCountByUser(userId: String): PostsCountByUser?
}
