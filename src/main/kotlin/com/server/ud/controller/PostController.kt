package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.entities.post.Post
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.service.post.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/post")
class PostController {

    @Autowired
    private lateinit var postService: PostService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun savePost(@RequestBody savePostRequest: SavePostRequest): SavedPostResponse? {
        return postService.savePost(savePostRequest)
    }

    @RequestMapping(value = ["/getPosts"], method = [RequestMethod.GET])
    fun getPosts(@RequestParam limit: Int, @RequestParam pagingState: String?): CassandraPageV2<Post?>? {
        return postService.getPosts(
            PaginatedRequest(
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getPost"], method = [RequestMethod.GET])
    fun getPost(@RequestParam postId: String): Post? {
        return postService.getPost(postId)
    }

    @RequestMapping(value = ["/getPostsCountByUser"], method = [RequestMethod.GET])
    fun getPostsCountByUser(@RequestParam userId: String): PostsCountByUser? {
        return postService.getPostsCountByUser(userId)
    }
}
