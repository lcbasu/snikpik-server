package com.server.ud.service.open_web

import com.server.ud.dto.*
import com.server.ud.entities.bookmark.BookmarksCountByResource
import com.server.ud.entities.comment.CommentsCountByPost
import com.server.ud.entities.like.LikesCountByResource
import com.server.ud.entities.social.FollowersCountByUser
import com.server.ud.entities.social.FollowingsCountByUser
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.provider.open_web.OpenWebProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OpenWebServiceImpl : OpenWebService() {

    @Autowired
    private lateinit var openWebProvider: OpenWebProvider

    override fun getUserDetails(userIdOrHandle: String): UserV2PublicMiniDataResponse {
        return openWebProvider.getUserDetails(userIdOrHandle)
    }

    override fun getPostsByUser(request: PostsByUserRequestV2): PostsByUserResponse {
        return openWebProvider.getPostsByUser(request)
    }

    override fun getPost(postId: String): SavedPostResponse {
        return openWebProvider.getPost(postId)
    }

    override fun getLikesCountByPost(postId: String): LikesCountByResource {
        return openWebProvider.getLikesCountByPost(postId)
    }

    override fun getBookmarksCountByPost(postId: String): BookmarksCountByResource {
        return openWebProvider.getBookmarksCountByPost(postId)
    }

    override fun getCommentsCountByPost(postId: String): CommentsCountByPost {
        return openWebProvider.getCommentsCountByPost(postId)
    }

    override fun getFollowersCountByUser(userIdOrHandle: String): FollowersCountByUser {
        return openWebProvider.getFollowersCountByUser(userIdOrHandle)
    }

    override fun getFollowingsCountByUser(userIdOrHandle: String): FollowingsCountByUser {
        return openWebProvider.getFollowingsCountByUser(userIdOrHandle)
    }

    override fun getPostsCountByUser(userIdOrHandle: String): PostsCountByUser {
        return openWebProvider.getPostsCountByUser(userIdOrHandle)
    }

}
