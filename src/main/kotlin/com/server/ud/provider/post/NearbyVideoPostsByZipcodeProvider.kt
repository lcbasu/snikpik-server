package com.server.ud.provider.post

import com.server.common.enums.MediaType
import com.server.common.utils.DateUtils
import com.server.ud.dao.post.NearbyVideoPostsByZipcodeRepository
import com.server.ud.dto.NearbyFeedRequest
import com.server.ud.entities.location.NearbyZipcodesByZipcode
import com.server.ud.entities.post.NearbyVideoPostsByZipcode
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getMediaDetails
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
class NearbyVideoPostsByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var nearbyVideoPostsByZipcodeRepository: NearbyVideoPostsByZipcodeRepository

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun saveWhileProcessing(nearbyPosts: List<NearbyVideoPostsByZipcode>, forNearbyZipcode: String): List<NearbyVideoPostsByZipcode> {
        try {
            val posts = nearbyPosts.map { post ->
                post.copy(zipcode = forNearbyZipcode)
            }
            return nearbyVideoPostsByZipcodeRepository.saveAll(posts)
        } catch (e: Exception) {
            logger.error("Saving NearbyPostsByZipcode failed forNearbyZipcode $forNearbyZipcode.")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun save(post: Post, nearbyZipcodes: List<NearbyZipcodesByZipcode>): List<NearbyVideoPostsByZipcode> {
        try {
            if (post.zipcode == null) {
                logger.warn("Post does not have location zipcode. Hence unable to save into NearbyVideoPostsByZipcode for postId: ${post.postId}.")
                return emptyList()
            }

            val hasVideo = post.getMediaDetails().media.filter { it.mediaType == MediaType.VIDEO }.isNotEmpty()

            if (hasVideo.not()) {
                logger.warn("Post does not have Video. Hence unable to save into NearbyVideoPostsByZipcode for postId: ${post.postId}.")
                return emptyList()
            }
            val posts = nearbyZipcodes.map {
                NearbyVideoPostsByZipcode(
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
            return nearbyVideoPostsByZipcodeRepository.saveAll(posts)
        } catch (e: Exception) {
            logger.error("Saving PostsByNearbyZipcode filed for ${post.postId}.")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun getNearbyVideoFeed(request: NearbyFeedRequest): CassandraPageV2<NearbyVideoPostsByZipcode> {
        return getPaginatedFeed(
            zipCode = request.zipcode,
            forDate = request.forDate,
            postType = PostType.GENERIC_POST,
            limit = request.limit,
            pagingState = request.pagingState,
        )
    }

    private fun getPaginatedFeed(zipCode: String, forDate: String, postType: PostType, limit: Int, pagingState: String?): CassandraPageV2<NearbyVideoPostsByZipcode> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = nearbyVideoPostsByZipcodeRepository.findAllByZipcodeAndPostTypeAndForDate(
            zipCode,
            postType,
            DateUtils.getInstantFromLocalDateTime(DateUtils.parseStandardDate(forDate)),
            pageRequest as Pageable
        )
        return CassandraPageV2(posts)
    }

}
