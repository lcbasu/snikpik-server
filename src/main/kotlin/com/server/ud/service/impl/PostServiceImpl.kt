package com.server.ud.service.impl

import com.server.common.provider.AuthProvider
import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.SavedPostResponse
import com.server.ud.dto.toSavedUserPostResponse
import com.server.ud.provider.PostProvider
import com.server.ud.service.PostService
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
        val post = postProvider.savePost(requestContext.user, savePostRequest)
        return post?.toSavedUserPostResponse()
    }

    override fun fakeSavePosts(): List<SavedPostResponse>? {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user

        if (user.absoluteMobile != "+919742097429") error("Only Admin is allowed to do this.")

        val posts = postProvider.fakeSaveUserPost(user)
        return posts.map {
            it.toSavedUserPostResponse()
        }
    }
}
