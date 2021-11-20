package com.server.ud.service.like

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.provider.like.LikeForResourceByUserProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.like.LikesCountByResourceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LikeServiceImpl : LikeService() {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var likeProvider: LikeProvider

    @Autowired
    private lateinit var likesCountByResourceProvider: LikesCountByResourceProvider

    @Autowired
    private lateinit var likeForResourceByUserProvider: LikeForResourceByUserProvider

    override fun saveLike(request: SaveLikeRequest): SavedLikeResponse? {
        val userDetailsFromToken = securityProvider.validateRequest()
        return likeProvider.save(userDetailsFromToken.getUid(), request)?.toSavedLikeResponse()
    }

    override fun getResourceLikesDetail(resourceId: String): ResourceLikesReportDetail {
        val userDetailsFromToken = securityProvider.validateRequest()
        val likesCountByResource = likesCountByResourceProvider.getLikesCountByResource(resourceId)?.likesCount ?: 0
        val liked = likeForResourceByUserProvider.getLikeForResourceByUser(
            resourceId = resourceId,
            userId = userDetailsFromToken.getUid()
        )?.liked ?: false
        return ResourceLikesReportDetail(
            resourceId = resourceId,
            likes = likesCountByResource,
            userLevelInfo = ResourceLikesReportDetailForUser(
                userId = userDetailsFromToken.getUid(),
                liked = liked
            )
        )
    }
}
