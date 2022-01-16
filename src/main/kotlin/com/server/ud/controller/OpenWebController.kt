package com.server.ud.controller

import com.server.ud.dto.PostsByUserRequestV2
import com.server.ud.dto.PostsByUserResponse
import com.server.ud.dto.SavedPostResponse
import com.server.ud.dto.UserV2PublicMiniDataResponse
import com.server.ud.entities.bookmark.BookmarksCountByResource
import com.server.ud.entities.comment.CommentsCountByPost
import com.server.ud.entities.like.LikesCountByResource
import com.server.ud.entities.social.FollowersCountByUser
import com.server.ud.entities.social.FollowingsCountByUser
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.service.open_web.OpenWebService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("ud/openWeb")
class OpenWebController {

    @Autowired
    private lateinit var openWebService: OpenWebService

    @RequestMapping(value = ["/getUserDetails"], method = [RequestMethod.GET])
    fun getUserDetails(@RequestParam userIdOrHandle: String): UserV2PublicMiniDataResponse {
        return openWebService.getUserDetails(userIdOrHandle)
    }

    @RequestMapping(value = ["/getFollowersCountByUser"], method = [RequestMethod.GET])
    fun getFollowersCountByUser(@RequestParam userIdOrHandle: String): FollowersCountByUser {
        return openWebService.getFollowersCountByUser(userIdOrHandle)
    }

    @RequestMapping(value = ["/getFollowingsCountByUser"], method = [RequestMethod.GET])
    fun getFollowingsCountByUser(@RequestParam userIdOrHandle: String): FollowingsCountByUser {
        return openWebService.getFollowingsCountByUser(userIdOrHandle)
    }

    @RequestMapping(value = ["/getPostsCountByUser"], method = [RequestMethod.GET])
    fun getPostsCountByUser(@RequestParam userIdOrHandle: String): PostsCountByUser {
        return openWebService.getPostsCountByUser(userIdOrHandle)
    }

    @RequestMapping(value = ["/getPostsByUser"], method = [RequestMethod.GET])
    fun getPostsByUser(@RequestParam userIdOrHandle: String,
                       @RequestParam limit: Int,
                       @RequestParam pagingState: String?): PostsByUserResponse {
        return openWebService.getPostsByUser(
            PostsByUserRequestV2(
                userIdOrHandle,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getPost"], method = [RequestMethod.GET])
    fun getPost(@RequestParam postId: String): SavedPostResponse {
        return openWebService.getPost(postId)
    }

    @RequestMapping(value = ["/getLikesCountByPost"], method = [RequestMethod.GET])
    fun getLikesCountByPost(@RequestParam postId: String): LikesCountByResource {
        return openWebService.getLikesCountByPost(postId)
    }

    @RequestMapping(value = ["/getBookmarksCountByPost"], method = [RequestMethod.GET])
    fun getBookmarksCountByPost(@RequestParam postId: String): BookmarksCountByResource {
        return openWebService.getBookmarksCountByPost(postId)
    }

    @RequestMapping(value = ["/getCommentsCountByPost"], method = [RequestMethod.GET])
    fun getCommentsCountByPost(@RequestParam postId: String): CommentsCountByPost {
        return openWebService.getCommentsCountByPost(postId)
    }
}
