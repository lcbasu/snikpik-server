package com.server.ud.provider.open_web

import com.server.ud.dto.*
import com.server.ud.entities.bookmark.BookmarksCountByResource
import com.server.ud.entities.comment.CommentsCountByPost
import com.server.ud.entities.like.LikesCountByResource
import com.server.ud.entities.social.FollowersCountByUser
import com.server.ud.entities.social.FollowingsCountByUser
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.provider.bookmark.BookmarksCountByResourceProvider
import com.server.ud.provider.comment.CommentsCountByPostProvider
import com.server.ud.provider.like.LikesCountByResourceProvider
import com.server.ud.provider.post.BookmarkedPostsByUserProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.post.PostsByUserProvider
import com.server.ud.provider.post.PostsCountByUserProvider
import com.server.ud.provider.social.FollowersCountByUserProvider
import com.server.ud.provider.social.FollowingsCountByUserProvider
import com.server.ud.provider.user.UserV2Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OpenWebProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var postsByUserProvider: PostsByUserProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var likesCountByResourceProvider: LikesCountByResourceProvider

    @Autowired
    private lateinit var bookmarksCountByResourceProvider: BookmarksCountByResourceProvider

    @Autowired
    private lateinit var commentsCountByPostProvider: CommentsCountByPostProvider

    @Autowired
    private lateinit var followersCountByUserProvider: FollowersCountByUserProvider

    @Autowired
    private lateinit var followingsCountByUserProvider: FollowingsCountByUserProvider

    @Autowired
    private lateinit var postsCountByUserProvider: PostsCountByUserProvider

    @Autowired
    private lateinit var bookmarkedPostsByUserProvider: BookmarkedPostsByUserProvider

    fun getUserDetails(userIdOrHandle: String): UserV2PublicMiniDataResponse {
        val user = userV2Provider.getUserByIdOrHandle(userIdOrHandle) ?: error("User not found for userIdOrHandle: $userIdOrHandle")
        return user.toUserV2PublicMiniDataResponse()
    }

    fun getPostsByUser(request: PostsByUserRequestV2): PostsByUserResponse {
        val user = userV2Provider.getUserByIdOrHandle(request.userIdOrHandle) ?: error("User not found for userIdOrHandle: ${request.userIdOrHandle}")
        return postsByUserProvider.getPostsByUserResponse(PostsByUserRequest(
            userId = user.userId,
            limit = request.limit,
            pagingState = request.pagingState
        ))
    }

    fun getPost(postId: String): SavedPostResponse {
        return postProvider.getPost(postId)?.toSavedPostResponse() ?: error("Post not found for postId: $postId")
    }

    fun getLikesCountByPost(postId: String): LikesCountByResource {
        return likesCountByResourceProvider.getLikesCountByResource(postId) ?: error("LikesCountByResource not found for postId: $postId")
    }

    fun getBookmarksCountByPost(postId: String): BookmarksCountByResource {
        return bookmarksCountByResourceProvider.getBookmarksCountByResource(postId) ?: error("BookmarksCountByResource not found for postId: $postId")
    }

    fun getCommentsCountByPost(postId: String): CommentsCountByPost {
        return commentsCountByPostProvider.getCommentsCountByPost(postId) ?: error("CommentsCountByPost not found for postId: $postId")
    }

    fun getFollowersCountByUser(userIdOrHandle: String): FollowersCountByUser {
        val user = userV2Provider.getUserByIdOrHandle(userIdOrHandle) ?: error("User not found for userIdOrHandle: $userIdOrHandle")
        return followersCountByUserProvider.getFollowersCountByUser(user.userId) ?: error("FollowersCountByUser not found for userId: ${user.userId}")
    }

    fun getFollowingsCountByUser(userIdOrHandle: String): FollowingsCountByUser {
        val user = userV2Provider.getUserByIdOrHandle(userIdOrHandle) ?: error("User not found for userIdOrHandle: $userIdOrHandle")
        return followingsCountByUserProvider.getFollowingsCountByUser(user.userId) ?: error("FollowingsCountByUser not found for userId: ${user.userId}")
    }

    fun getPostsCountByUser(userIdOrHandle: String): PostsCountByUser {
        val user = userV2Provider.getUserByIdOrHandle(userIdOrHandle) ?: error("User not found for userIdOrHandle: $userIdOrHandle")
        return postsCountByUserProvider.getPostsCountByUser(user.userId) ?: error("PostsCountByUser not found for userId: ${user.userId}")
    }

    fun getBookmarkedPostsByUser(request: BookmarkedPostsByUserRequestV2): BookmarkedPostsByUserResponse {
        val user = userV2Provider.getUserByIdOrHandle(request.userIdOrHandle) ?: error("User not found for userIdOrHandle: ${request.userIdOrHandle}")
        return bookmarkedPostsByUserProvider.getBookmarkedPostsByUserResponse(BookmarkedPostsByUserRequest(
            userId = user.userId,
            limit = request.limit,
            pagingState = request.pagingState
        ))
    }

}
