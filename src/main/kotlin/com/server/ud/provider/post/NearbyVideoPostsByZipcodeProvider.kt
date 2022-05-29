package com.server.ud.provider.post

import com.server.common.enums.MediaType
import com.server.common.provider.SecurityProvider
import com.server.ud.dao.post.NearbyVideoPostsByZipcodeRepository
import com.server.ud.dao.post.NearbyVideoPostsByZipcodeTrackerRepository
import com.server.ud.dto.NearbyFeedRequest
import com.server.ud.dto.VideoFeedViewResultList
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
class NearbyVideoPostsByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var nearbyVideoPostsByZipcodeRepository: NearbyVideoPostsByZipcodeRepository

    @Autowired
    private lateinit var nearbyVideoPostsByZipcodeTrackerRepository: NearbyVideoPostsByZipcodeTrackerRepository

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var udCacheProvider: UDCacheProviderV2

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun saveWhileProcessing(nearbyPosts: List<NearbyVideoPostsByZipcode>, forNearbyZipcode: String): List<NearbyVideoPostsByZipcode> {
        try {
            val posts = nearbyPosts.map { post ->
                post.copy(zipcode = forNearbyZipcode)
            }
            return saveAll(posts)
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
            logger.error("Saving PostsByNearbyZipcode failed for ${post.postId}.")
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

    fun deleteOldPosts(zipcodes: Set<String>, postType: PostType, postId: String) {
        val posts = mutableListOf<NearbyVideoPostsByZipcode>()
        zipcodes.map {
            posts.addAll(nearbyVideoPostsByZipcodeRepository.getAll(
                it, postType,
                postId)
            )
        }

        posts.chunked(5).map {
            nearbyVideoPostsByZipcodeRepository.deleteAll(it)
        }
        logger.info("Deleted NearbyVideoPostsByZipcode ${posts.size} posts for postId: $postId")
    }

    fun getAllPostsTracker(postId: String): List<NearbyVideoPostsByZipcodeTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<NearbyVideoPostsByZipcodeTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = nearbyVideoPostsByZipcodeTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = nearbyVideoPostsByZipcodeTrackerRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedPosts.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedPosts
    }

    fun deletePostExpandedData(postId: String) {
        val trackedPosts = getAllPostsTracker(postId)
        logger.info("Deleting post ${postId} from NearbyVideoPostsByZipcode. Total ${trackedPosts.size} zipcode x posts entries needs to be deleted.")
        trackedPosts.chunked(5).map {
            nearbyVideoPostsByZipcodeRepository.deleteAll(it.map { it.toNearbyVideoPostsByZipcode() })
        }
        logger.info("Deleted all entries ${trackedPosts.size} for zipcode x posts for post ${postId} from NearbyVideoPostsByZipcode.")
    }

    fun processPostExpandedData(post: Post) {
        runBlocking {
            if (post.zipcode != null) {
                val nearbyZipcodes = nearbyZipcodesByZipcodeProvider.getNearbyZipcodesByZipcode(post.zipcode!!)
                save(post, nearbyZipcodes)
            }
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

    fun getNearbyFeed(request: NearbyFeedRequest): VideoFeedViewResultList {
        val result = getNearbyVideoFeed(request)
        val userId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse()
        val blockedIds = userId?.let {
            udCacheProvider.getBlockedIds(userId) ?: BlockedIDs()
        } ?: BlockedIDs()
        val posts = result.content?.filterNotNull()?.filterNot {
            it.postId in blockedIds.postIds || it.userId in blockedIds.userIds || it.userId in blockedIds.mutedUserIds
        }?.map { it.toSavedPostResponse() } ?: emptyList()
        return VideoFeedViewResultList(
            posts = posts,
            count = posts.size,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    fun saveAll(posts: List<NearbyVideoPostsByZipcode>): List<NearbyVideoPostsByZipcode> {
        val savedPosts = nearbyVideoPostsByZipcodeRepository.saveAll(posts)
        nearbyVideoPostsByZipcodeTrackerRepository.saveAll(savedPosts.map { it.toNearbyVideoPostsByZipcodeTracker() })
        return savedPosts
    }
}
