package com.server.ud.service.post

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.entities.post.Post
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.pagination.CassandraPageV2
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
    private lateinit var postsCountByUserProvider: PostsCountByUserProvider

    override fun savePost(savePostRequest: SavePostRequest): SavedPostResponse? {
        val requestContext = securityProvider.validateRequest()
        val post = postProvider.save(requestContext.getUserIdToUse(), savePostRequest)
        return post?.toSavedUserPostResponse()
    }

    override fun fakeSavePosts(request: FakePostRequest): List<SavedPostResponse>? {
        val requestContext = securityProvider.validateRequest()
//        val user = requestContext.userV2
//
//        if (user.absoluteMobile != "+919742097429") error("Only Admin is allowed to do this.")

        if (request.countOfPost > 25) error("Only 25 posts are allowed to be created at one time")
        if (request.countOfPost < 1) error("Minimum 1 post needs to be created")

        val posts = postProvider.fakeSave(requestContext.getUserIdToUse(), request.countOfPost)
        return posts.map {
            it.toSavedUserPostResponse()
        }
    }

    override fun getPosts(request: PaginatedRequest): CassandraPageV2<Post?>? {
        return postProvider.getPosts(request)
    }

    override fun getPost(postId: String): Post? {
        return postProvider.getPost(postId)
    }

    override fun getPostsCountByUser(userId: String): PostsCountByUser? {
        return postsCountByUserProvider.getPostsCountByUser(userId)
    }
}
