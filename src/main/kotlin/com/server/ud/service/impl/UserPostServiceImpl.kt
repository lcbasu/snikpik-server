package com.server.ud.service.impl

import com.server.common.provider.AuthProvider
import com.server.ud.dto.SaveUserPostRequest
import com.server.ud.dto.SavedUserPostResponse
import com.server.ud.dto.toSavedUserPostResponse
import com.server.ud.provider.UserPostProvider
import com.server.ud.service.UserPostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserPostServiceImpl : UserPostService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var userPostProvider: UserPostProvider

    override fun saveUserPost(saveUserPostRequest: SaveUserPostRequest): SavedUserPostResponse? {
        val requestContext = authProvider.validateRequest()
        val post = userPostProvider.savePost(requestContext.user, saveUserPostRequest)
        return post?.toSavedUserPostResponse()
    }

    override fun fakeSaveUserPost(): List<SavedUserPostResponse>? {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user

        if (user.absoluteMobile != "+919742097429") error("Only Admin is allowed to do this.")

        val posts = userPostProvider.fakeSaveUserPost(user)
        return posts.map {
            it.toSavedUserPostResponse()
        }
    }
}
