package com.server.ud.service.post

import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like

abstract class LikeService {
    abstract fun saveLike(saveLikeRequest: SaveLikeRequest): Like?
}
