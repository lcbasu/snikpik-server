package com.server.ud.controller

import com.server.ud.dto.FakePostRequest
import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.SavedPostResponse
import com.server.ud.service.post.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("userPost")
class PostController {
    @Autowired
    private lateinit var postService: PostService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun savePost(@RequestBody savePostRequest: SavePostRequest): SavedPostResponse? {
        return postService.savePost(savePostRequest)
    }

    @RequestMapping(value = ["/fakeSave"], method = [RequestMethod.POST])
    fun fakeSavePostsWithCount(@RequestBody request: FakePostRequest): List<SavedPostResponse>? {
        return postService.fakeSavePosts(request)
    }
}
