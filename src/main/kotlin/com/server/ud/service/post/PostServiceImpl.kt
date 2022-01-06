package com.server.ud.service.post

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.entities.post.Post
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.post.DeletePostProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.post.PostsCountByUserProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostServiceImpl : PostService() {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var deletePostProvider: DeletePostProvider

    @Autowired
    private lateinit var postsCountByUserProvider: PostsCountByUserProvider

    override fun savePost(savePostRequest: SavePostRequest): SavedPostResponse? {
        val requestContext = securityProvider.validateRequest()
        val post = postProvider.save(requestContext.getUserIdToUse(), savePostRequest)
        return post?.toSavedPostResponse()
    }

    override fun getPosts(request: PaginatedRequest): CassandraPageV2<Post?>? {
        return postProvider.getPosts(request)
    }

    override fun getPost(postId: String): SavedPostResponse? {
        return postProvider.getPost(postId)?.toSavedPostResponse()
    }

    override fun getPostsCountByUser(userId: String): PostsCountByUser? {
        return postsCountByUserProvider.getPostsCountByUser(userId)
    }

    override fun deletePost(request: DeletePostRequest): Boolean {
        postProvider.deletePost(request.postId)
        return true;
    }
}
