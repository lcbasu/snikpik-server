package com.server.ud.service.post

import com.server.common.provider.AuthProvider
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like
import com.server.ud.provider.like.LikeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LikeServiceImpl : LikeService() {

    @Autowired
    private lateinit var likeProvider: LikeProvider

    @Autowired
    private lateinit var authProvider: AuthProvider

    override fun saveLike(request: SaveLikeRequest): Like? {
        val requestContext = authProvider.validateRequest()
        return likeProvider.save(requestContext.userV2, request)
    }
}
