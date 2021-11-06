package com.server.ud.service.post

import com.server.common.provider.AuthProvider
import com.server.ud.dto.ResourceLikesDetail
import com.server.ud.dto.ResourceLikesDetailForUser
import com.server.ud.dto.SaveLikeRequest
import com.server.ud.entities.like.Like
import com.server.ud.provider.like.LikeForResourceByUserProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.like.LikesCountByResourceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LikeServiceImpl : LikeService() {

    @Autowired
    private lateinit var likeProvider: LikeProvider

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var likesCountByResourceProvider: LikesCountByResourceProvider

    @Autowired
    private lateinit var likeForResourceByUserProvider: LikeForResourceByUserProvider

    override fun saveLike(request: SaveLikeRequest): Like? {
        val requestContext = authProvider.validateRequest()
        return likeProvider.save(requestContext.userV2, request)
    }

    override fun getResourceLikesDetail(resourceId: String): ResourceLikesDetail {
        val requestContext = authProvider.validateRequest()
        val likesCountByResource = likesCountByResourceProvider.getLikesCountByResource(resourceId)?.likesCount ?: 0
        val liked = likeForResourceByUserProvider.getLikeForResourceByUser(
            resourceId = resourceId,
            userId = requestContext.userV2.userId
        )?.liked ?: false
        return ResourceLikesDetail(
            resourceId = resourceId,
            likes = likesCountByResource,
            userLevelInfo = ResourceLikesDetailForUser(
                userId = requestContext.userV2.userId,
                liked = liked
            )
        )
    }
}
