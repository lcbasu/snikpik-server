package com.server.ud.controller

import com.server.ud.dto.ResourceLikesDetail
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like
import com.server.ud.service.post.LikeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/like")
class LikeController {

    @Autowired
    private lateinit var likeService: LikeService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveLike(@RequestBody request: SaveLikeRequest): Like? {
        return likeService.saveLike(request)
    }

    @RequestMapping(value = ["/getResourceLikesDetail"], method = [RequestMethod.GET])
    fun getResourceLikesDetail(@RequestParam resourceId: String): ResourceLikesDetail {
        return likeService.getResourceLikesDetail(resourceId)
    }
}
