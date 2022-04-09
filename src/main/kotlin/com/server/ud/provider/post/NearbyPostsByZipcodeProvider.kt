package com.server.ud.provider.post

import com.server.common.provider.SecurityProvider
import com.server.ud.dao.post.NearbyPostsByZipcodeRepository
import com.server.ud.dao.post.NearbyPostsByZipcodeTrackerRepository
import com.server.ud.dto.CommunityWallFeedRequest
import com.server.ud.dto.CommunityWallViewResponse
import com.server.ud.dto.NearbyFeedRequest
import com.server.ud.dto.toSavedPostResponse
import com.server.ud.entities.location.NearbyZipcodesByZipcode
import com.server.ud.entities.post.*
import com.server.ud.enums.PostType
import com.server.ud.enums.ProcessingType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.cache.BlockedIDs
import com.server.ud.provider.cache.UDCacheProviderV2
import com.server.ud.provider.location.NearbyZipcodesByZipcodeProvider
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    private lateinit var nearbyPostsByZipcodeTrackerRepository: NearbyPostsByZipcodeTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var udCacheProvider: UDCacheProviderV2

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun save(nearbyPosts: List<NearbyPostsByZipcode>, forNearbyZipcode: String): List<NearbyPostsByZipcode> {
        try {
            val posts = nearbyPosts.map { post ->
                post.copy(zipcode = forNearbyZipcode)
            }
            return saveAll(posts)
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
                    sourceMedia = post.sourceMedia,
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
            return saveAll(posts)
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

    fun deleteOldPosts(zipcodes: Set<String>, postType: PostType, postId: String) {
        val posts = mutableListOf<NearbyPostsByZipcode>()
        zipcodes.map {
            posts.addAll(nearbyPostsByZipcodeRepository.getAll(
                it, postType,
                postId)
            )
        }

        posts.chunked(5).map {
            nearbyPostsByZipcodeRepository.deleteAll(it)
        }
        logger.info("Deleted NearbyPostsByZipcode ${posts.size} posts for postId: $postId")
    }

    fun getAllPostsTracker(postId: String): List<NearbyPostsByZipcodeTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<NearbyPostsByZipcodeTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = nearbyPostsByZipcodeTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        while (hasNext) {
            pagingState = slicedResult.pagingState ?: ""
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = nearbyPostsByZipcodeTrackerRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            trackedPosts.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedPosts
    }

    fun deletePostExpandedData(postId: String) {
        val trackedPosts = getAllPostsTracker(postId)
        logger.info("Deleting post ${postId} from NearbyPostsByZipcode. Total ${trackedPosts.size} zipcode x posts entries needs to be deleted.")
        trackedPosts.chunked(5).map {
            nearbyPostsByZipcodeRepository.deleteAll(
                it.map { it.toNearbyPostsByZipcode() }
            )
        }
        logger.info("Deleted all entries : ${trackedPosts.size} for zipcode x posts for post ${postId} from NearbyPostsByZipcode.")
    }

    fun processPostExpandedData(post: Post) {
        runBlocking {
            val nearbyZipcodes = nearbyZipcodesByZipcodeProvider.getNearbyZipcodesByZipcode(post.zipcode!!)
            save(post, nearbyZipcodes)
        }
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            when (processingType) {
                ProcessingType.NO_PROCESSING -> logger.error("This should not happen. Updating the zipcode without processing should never happen.")
                ProcessingType.DELETE_AND_REFRESH -> {
                    // Delete old data
                    deletePostExpandedData(postUpdate.oldPost.postId)
                    // Index the new data
                    processPostExpandedData(postUpdate.newPost!!)
                }
                ProcessingType.REFRESH -> {
                    // Index the new data
                    processPostExpandedData(postUpdate.newPost!!)
                }
            }
        }
    }

    fun getFeed(request: CommunityWallFeedRequest): CommunityWallViewResponse {
        val result = getCommunityWallFeed(request)
        val userId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse()
        val blockedIds = userId?.let {
            udCacheProvider.getBlockedIds(userId) ?: BlockedIDs()
        } ?: BlockedIDs()
        val posts = result.content?.filterNotNull()?.filterNot {
            it.postId in blockedIds.postIds || it.userId in blockedIds.userIds || it.userId in blockedIds.mutedUserIds
        }?.map { it.toSavedPostResponse() } ?: emptyList()
        return CommunityWallViewResponse(
            posts = posts,
            count = posts.size,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    private fun saveAll(posts: List<NearbyPostsByZipcode>): List<NearbyPostsByZipcode> {
        val savedPosts = nearbyPostsByZipcodeRepository.saveAll(posts)
        nearbyPostsByZipcodeTrackerRepository.saveAll(savedPosts.map { it.toNearbyPostsByZipcodeTracker() })
        return savedPosts
    }
}
