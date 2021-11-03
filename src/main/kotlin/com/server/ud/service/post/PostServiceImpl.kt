package com.server.ud.service.post

import com.server.common.provider.AuthProvider
import com.server.ud.dto.*
import com.server.ud.entities.post.Post
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.post.PostProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostServiceImpl : PostService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    override fun savePost(savePostRequest: SavePostRequest): SavedPostResponse? {
        val requestContext = authProvider.validateRequest()
        val post = postProvider.save(requestContext.user, savePostRequest)
        return post?.toSavedUserPostResponse()
    }

    override fun fakeSavePosts(request: FakePostRequest): List<SavedPostResponse>? {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user

        if (user.absoluteMobile != "+919742097429") error("Only Admin is allowed to do this.")

        if (request.countOfPost > 25) error("Only 25 posts are allowed to be created at one time")
        if (request.countOfPost < 1) error("Minimum 1 post needs to be created")

        val posts = postProvider.fakeSave(user, request.countOfPost)
        return posts.map {
            it.toSavedUserPostResponse()
        }
    }

    override fun getPosts(request: PaginatedRequest): CassandraPageV2<Post?>? {
        return postProvider.getPosts(request)
    }
}
