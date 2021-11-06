package com.server.ud.service.post

import com.server.ud.dto.ResourceLikesDetail
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like

abstract class LikeService {
    abstract fun saveLike(request: SaveLikeRequest): Like?
    abstract fun getResourceLikesDetail(resourceId: String): ResourceLikesDetail
}
