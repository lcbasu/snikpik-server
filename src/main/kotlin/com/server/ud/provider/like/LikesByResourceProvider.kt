package com.server.ud.provider.like

import com.server.ud.dao.like.LikesByResourceRepository
import com.server.ud.dao.like.LikesByResourceTrackerRepository
import com.server.ud.entities.like.*
import com.server.ud.enums.ResourceType
import com.server.common.pagination.CassandraPageV2
import com.server.common.utils.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class LikesByResourceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likesByResourceRepository: LikesByResourceRepository

    @Autowired
    private lateinit var likesByResourceTrackerRepository: LikesByResourceTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(like: Like) : LikesByResource? {
        try {
            val likesByResource = LikesByResource(
                resourceId = like.resourceId,
                resourceType = like.resourceType,
                createdAt = like.createdAt,
                userId = like.userId,
                likeId = like.likeId,
                liked = like.liked,
            )
            val savedLikesByResource = likesByResourceRepository.save(likesByResource)
            logger.info("Saved LikesByResource into cassandra for likeId: ${like.likeId}")
            likesByResourceTrackerRepository.save(savedLikesByResource.toLikesByResourceTracker())
            return savedLikesByResource
        } catch (e: Exception) {
            logger.error("Saving LikesByResource into cassandra failed for likeId: ${like.likeId}")
            e.printStackTrace()
            return null
        }
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }


    fun getAllLikesByResourceTracker(resourceId: String): List<LikesByResourceTracker> {
        val limit = 10
        var pagingState = ""

        val trackedLikes = mutableListOf<LikesByResourceTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = likesByResourceTrackerRepository.findAllByResourceId(
            resourceId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedLikes.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextLikes = likesByResourceTrackerRepository.findAllByResourceId(
                resourceId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextLikes)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedLikes.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedLikes
    }

    fun getAllLikesByResource(resourceId: String) : List<LikesByResource> {
        val trackedLikes = getAllLikesByResourceTracker(resourceId)
        val posts = mutableListOf<LikesByResource>()
        return trackedLikes.map {
            it.toLikesByResource()
        }
//        return posts
    }

    fun deleteAll(resourceId: String, resourceType: ResourceType) {
        likesByResourceRepository.deleteAllByResourceIdAndResourceType(resourceId, resourceType)
    }
}
