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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private lateinit var zipcodeByPostProvider: ZipcodeByPostProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun saveWhileProcessing(nearbyPosts: List<NearbyVideoPostsByZipcode>, forNearbyZipcode: String): List<NearbyVideoPostsByZipcode> {
        try {
            val posts = nearbyPosts.map { post ->
                post.copy(zipcode = forNearbyZipcode)
            }
            return nearbyVideoPostsByZipcodeRepository.saveAll(posts)
        } catch (e: Exception) {
            logger.error("Saving NearbyVideoPostsByZipcode failed forNearbyZipcode $forNearbyZipcode.")
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
                    locality = post.locality,
                    subLocality = post.subLocality,
                    route = post.route,
                    city = post.city,
                    state = post.state,
                    country = post.country,
                    countryCode = post.countryCode,
                    completeAddress = post.completeAddress,
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
            postType = PostType.GENERIC_POST,
            limit = request.limit,
            pagingState = request.pagingState,
        )
    }

    private fun getPaginatedFeed(zipCode: String, postType: PostType, limit: Int, pagingState: String?): CassandraPageV2<NearbyVideoPostsByZipcode> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = nearbyVideoPostsByZipcodeRepository.findAllByZipcodeAndPostType(
            zipCode,
            postType,
            pageRequest as Pageable
        )
        return CassandraPageV2(posts)
    }

    fun deletePostExpandedData(post: Post) {
        GlobalScope.launch {
            val zipcodesByPost = zipcodeByPostProvider.getZipcodesByPost(post.postId)
            val posts = mutableListOf<NearbyVideoPostsByZipcode>()
            zipcodesByPost.map {
                val zipcode = it.zipcode
                val postType = post.postType
                val createdAt = post.createdAt
                val postId = post.postId
                posts.addAll(
                    nearbyVideoPostsByZipcodeRepository.findAllByZipcodeAndPostTypeAndCreatedAtAndPostId(
                        zipcode,
                        postType,
                        createdAt,
                        postId
                    )
                )
            }
            val maxDeleteSize = 5
            logger.info("Deleting post ${post.postId} from NearbyVideoPostsByZipcode. Total ${posts.size} zipcode x posts entries needs to be deleted.")
            posts.chunked(maxDeleteSize).map {
                nearbyVideoPostsByZipcodeRepository.deleteAll(it)
                logger.info("Deleted maxDeleteSize: ${it.size} zipcode x posts entries.")
            }
            logger.info("Deleted all entries for zipcode x posts for post ${post.postId} from NearbyVideoPostsByZipcode.")
        }
    }
}
