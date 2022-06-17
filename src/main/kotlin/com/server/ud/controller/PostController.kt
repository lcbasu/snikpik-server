package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.entities.user.PostsCountByUser
import com.server.ud.service.post.PostService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/post")
class PostController {

    @Autowired
    private lateinit var postService: PostService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun savePost(@RequestBody savePostRequest: SavePostRequest): SavedPostResponse? {
        return postService.savePost(savePostRequest)
    }

    @RequestMapping(value = ["/delete"], method = [RequestMethod.POST])
    fun deletePost(@RequestBody request: DeletePostRequest): Boolean {
        return postService.deletePost(request)
    }


    @RequestMapping(value = ["/update"], method = [RequestMethod.POST])
    fun update(@RequestBody request: UpdatePostRequest): SavedPostResponse? {
        return postService.update(request)
    }

    @RequestMapping(value = ["/deletePostFromExplore"], method = [RequestMethod.POST])
    fun deletePostFromExplore(@RequestBody request: DeletePostRequest): Boolean {
        return postService.deletePostFromExplore(request)
    }

//    @RequestMapping(value = ["/getPosts"], method = [RequestMethod.GET])
//    fun getPosts(@RequestParam limit: Int, @RequestParam pagingState: String?): CassandraPageV2<Post?>? {
//        return postService.getPosts(
//            PaginatedRequest(
//                limit,
//                pagingState
//            )
//        )
//    }

    @RequestMapping(value = ["/getPost"], method = [RequestMethod.GET])
    fun getPost(@RequestParam postId: String): SavedPostResponse? {
        return postService.getPost(postId)
    }

    @RequestMapping(value = ["/getPostsCountByUser"], method = [RequestMethod.GET])
    fun getPostsCountByUser(@RequestParam userId: String): PostsCountByUser? {
        return postService.getPostsCountByUser(userId)
    }

    @RequestMapping(value = ["/report"], method = [RequestMethod.POST])
    fun report(@RequestBody request: PostReportRequest): PostReportResponse? {
        return postService.report(request)
    }

    @RequestMapping(value = ["/getAllReport"], method = [RequestMethod.GET])
    fun getAllReport(@RequestParam userId: String): AllPostReportResponse? {
        return postService.getAllReport(userId)
    }
}
