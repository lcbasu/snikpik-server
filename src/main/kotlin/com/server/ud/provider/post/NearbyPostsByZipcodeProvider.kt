package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.NearbyPostsByZipcodeRepository
import com.server.ud.dto.CommunityWallFeedRequest
import com.server.ud.dto.NearbyFeedRequest
import com.server.ud.entities.location.NearbyZipcodesByZipcode
import com.server.ud.entities.post.NearbyPostsByZipcode
import com.server.ud.entities.post.Post
import com.server.ud.enums.PostType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.location.NearbyZipcodesByZipcodeProvider
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class NearbyPostsByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var nearbyPostsByZipcodeRepository: NearbyPostsByZipcodeRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(nearbyPosts: List<NearbyPostsByZipcode>, forNearbyZipcode: String): List<NearbyPostsByZipcode> {
        try {
            val posts = nearbyPosts.map { post ->
                post.copy(zipcode = forNearbyZipcode)
            }
            return nearbyPostsByZipcodeRepository.saveAll(posts)
        } catch (e: Exception) {
            logger.error("Saving NearbyPostsByZipcode failed forNearbyZipcode $forNearbyZipcode.")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun save(post: Post, nearbyZipcodes: List<NearbyZipcodesByZipcode>): List<NearbyPostsByZipcode> {
        try {
            if (post.zipcode == null) {
                logger.error("Post does not have location zipcode. Hence unable to save into NearbyPostsByZipcode for postId: ${post.postId}.")
                return emptyList()
            }
            val posts = nearbyZipcodes.map {
                NearbyPostsByZipcode(
                    zipcode = it.nearbyZipcode,
                    forDate = DateUtils.getInstantDate(post.createdAt),
                    createdAt = post.createdAt,
                    postId = post.postId,
                    postType = post.postType,
                    userId = post.userId,
                    originalZipcode = post.zipcode!!,
                    locationId = post.locationId,
                    locationName = post.locationName,
                    locationLat = post.locationLat,
                    locationLng = post.locationLng,
                    title = post.title,
                    description = post.description,
                    media = post.media,
                    tags = post.tags,
                    categories = post.categories,
                )
            }
            return nearbyPostsByZipcodeRepository.saveAll(posts)
        } catch (e: Exception) {
            logger.error("Saving PostsByNearbyZipcode filed for ${post.postId}.")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun getNearbyFeed(request: NearbyFeedRequest): CassandraPageV2<NearbyPostsByZipcode> {
        return getPaginatedFeed(
            zipCode = request.zipcode,
            forDate = request.forDate,
            postType = PostType.GENERIC_POST,
            limit = request.limit,
            pagingState = request.pagingState,
        )
    }

    fun getCommunityWallFeed(request: CommunityWallFeedRequest): CassandraPageV2<NearbyPostsByZipcode> {
        return getPaginatedFeed(
            zipCode = request.zipcode,
            forDate = request.forDate,
            postType = PostType.COMMUNITY_WALL_POST,
            limit = request.limit,
            pagingState = request.pagingState,
        )
    }

    fun getPaginatedFeed(zipCode: String, forDate: String, postType: PostType, limit: Int, pagingState: String? = null): CassandraPageV2<NearbyPostsByZipcode> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = nearbyPostsByZipcodeRepository.findAllByZipcodeAndPostTypeAndForDate(
            zipCode,
            postType,
            DateUtils.getInstantFromLocalDateTime(DateUtils.parseStandardDate(forDate)),
            pageRequest as Pageable
        )
        return CassandraPageV2(posts)
    }

    fun deletePost(postId: String) {
        TODO("Add steps to delete post and related information")
    }

}
