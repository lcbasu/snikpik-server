package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.dk.model.getMediaPresenceType
import com.server.ud.dao.post.NearbyPostsByZipcodeRepository
import com.server.ud.dto.NearbyFeedRequest
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
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(post: Post): List<NearbyPostsByZipcode> {
        try {
            if (post.zipcode == null) {
                logger.error("Post does not have location zipcode. Hence unable to save into NearbyPostsByZipcode for postId: ${post.postId}.")
                return emptyList()
            }
            val nearbyZipcodes = nearbyZipcodesByZipcodeProvider.getNearbyZipcodesByZipcode(post.zipcode!!)
            val posts = nearbyZipcodes.map {
                NearbyPostsByZipcode(
                    zipcode = it.nearbyZipcode,
                    forDate = DateUtils.getInstantDate(post.createdAt),
                    createdAt = post.createdAt,
                    postId = post.postId,
                    postType = post.postType,
                    userId = post.userId,
                    mediaPresenceType = post.mediaPresenceType,
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
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val posts = nearbyPostsByZipcodeRepository.findAllByZipcodeAndPostTypeAndForDate(request.zipcode, PostType.GENERIC_POST, DateUtils.getInstantFromLocalDateTime(DateUtils.parseStandardDate(request.forDate)), pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

}
