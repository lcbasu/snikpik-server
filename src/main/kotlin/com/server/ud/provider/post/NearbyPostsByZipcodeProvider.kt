package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.NearbyPostsByZipcodeRepository
import com.server.ud.dto.CommunityWallFeedRequest
import com.server.ud.dto.NearbyFeedRequest
import com.server.ud.entities.location.NearbyZipcodesByZipcode
import com.server.ud.entities.post.NearbyPostsByZipcode
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.toNearbyVideoPostsByZipcode
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
class NearbyPostsByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var nearbyPostsByZipcodeRepository: NearbyPostsByZipcodeRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var zipcodeByPostProvider: ZipcodeByPostProvider

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
            postType = PostType.GENERIC_POST,
            limit = request.limit,
            pagingState = request.pagingState,
        )
    }

    fun getCommunityWallFeed(request: CommunityWallFeedRequest): CassandraPageV2<NearbyPostsByZipcode> {
        return getPaginatedFeed(
            zipCode = request.zipcode,
            postType = PostType.COMMUNITY_WALL_POST,
            limit = request.limit,
            pagingState = request.pagingState,
        )
    }

    fun getPaginatedFeed(zipCode: String, postType: PostType, limit: Int, pagingState: String? = null): CassandraPageV2<NearbyPostsByZipcode> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = nearbyPostsByZipcodeRepository.findAllByZipcodeAndPostType(
            zipCode,
            postType,
            pageRequest as Pageable
        )
        return CassandraPageV2(posts)
    }

    fun deletePostExpandedData(post: Post) {
        GlobalScope.launch {

            val zipcodesByPost = zipcodeByPostProvider.getZipcodesByPost(post.postId)

            val posts = mutableListOf<NearbyPostsByZipcode>()
            val postType = post.postType
            val createdAt = post.createdAt
            val postId = post.postId
            zipcodesByPost.map {
                val zipcode = it.zipcode
                posts.addAll(
                    nearbyPostsByZipcodeRepository.findAllByZipcodeAndPostTypeAndCreatedAtAndPostId(
                        zipcode,
                        postType,
                        createdAt,
                        postId
                    )
                )
            }
            post.zipcode?.let {
                posts.addAll(
                    nearbyPostsByZipcodeRepository.findAllByZipcodeAndPostTypeAndCreatedAtAndPostId(
                        it,
                        postType,
                        createdAt,
                        postId
                    )
                )
            }
            val maxDeleteSize = 5
            logger.info("Deleting post ${post.postId} from NearbyPostsByZipcode. Total ${posts.size} zipcode x posts entries needs to be deleted.")
            posts.chunked(maxDeleteSize).map {
                nearbyPostsByZipcodeRepository.deleteAll(it)
                logger.info("Deleted maxDeleteSize: ${it.size} zipcode x posts entries.")
            }
            logger.info("Deleted all entries for zipcode x posts for post ${post.postId} from NearbyPostsByZipcode.")
        }
    }

}
