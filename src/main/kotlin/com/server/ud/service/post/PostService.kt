package com.server.ud.service.post

import com.server.ud.dto.*
import com.server.ud.entities.post.Post
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.pagination.CassandraPageV2

abstract class PostService {
    abstract fun savePost(savePostRequest: SavePostRequest): SavedPostResponse?
    abstract fun getPosts(request: PaginatedRequest): CassandraPageV2<Post?>?
    abstract fun getPost(postId: String): SavedPostResponse?
    abstract fun getPostsCountByUser(userId: String): PostsCountByUser?
    abstract fun deletePost(request: DeletePostRequest): Boolean
    abstract fun deletePostFromExplore(request: DeletePostRequest): Boolean
    abstract fun update(request: UpdatePostRequest): SavedPostResponse?
    abstract fun report(request: PostReportRequest): PostReportResponse?
    abstract fun getAllReport(userId: String): AllPostReportResponse?
}
