package com.server.ud.service.like

import com.server.ud.dto.ResourceLikesReportDetail
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like

abstract class LikeService {
    abstract fun saveLike(request: SaveLikeRequest): Like?
    abstract fun getResourceLikesDetail(resourceId: String): ResourceLikesReportDetail
}
