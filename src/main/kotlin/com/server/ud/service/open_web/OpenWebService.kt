package com.server.ud.service.open_web

import com.server.ud.dto.*
import com.server.ud.entities.bookmark.BookmarksCountByResource
import com.server.ud.entities.comment.CommentsCountByPost
import com.server.ud.entities.like.LikesCountByResource
import com.server.ud.entities.social.FollowersCountByUser
import com.server.ud.entities.social.FollowingsCountByUser
import com.server.ud.entities.user.PostsCountByUser

abstract class OpenWebService {
    abstract fun getUserDetails(userIdOrHandle: String): UserV2PublicMiniDataResponse
    abstract fun getPostsByUser(request: PostsByUserRequestV2): PostsByUserResponse
    abstract fun getPost(postId: String): SavedPostResponse
    abstract fun getLikesCountByPost(postId: String): LikesCountByResource
    abstract fun getBookmarksCountByPost(postId: String): BookmarksCountByResource
    abstract fun getCommentsCountByPost(postId: String): CommentsCountByPost
    abstract fun getFollowersCountByUser(userIdOrHandle: String): FollowersCountByUser
    abstract fun getFollowingsCountByUser(userIdOrHandle: String): FollowingsCountByUser
    abstract fun getPostsCountByUser(userIdOrHandle: String): PostsCountByUser
}
