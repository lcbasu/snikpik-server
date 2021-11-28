package com.server.ud.controller

import com.server.ud.dto.ResourceLikesReportDetail
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.dto.SavedLikeResponse
import com.server.ud.service.like.LikeService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/like")
class LikeController {

    @Autowired
    private lateinit var likeService: LikeService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveLike(@RequestBody request: SaveLikeRequest): SavedLikeResponse? {
        return likeService.saveLike(request)
    }

    @RequestMapping(value = ["/getResourceLikesDetail"], method = [RequestMethod.GET])
    fun getResourceLikesDetail(@RequestParam resourceId: String): ResourceLikesReportDetail {
        return likeService.getResourceLikesDetail(resourceId)
    }
}
