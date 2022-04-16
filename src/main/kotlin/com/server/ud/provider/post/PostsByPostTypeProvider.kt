package com.server.ud.provider.post

import com.server.common.provider.SecurityProvider
import com.server.ud.dao.post.PostsByPostTypeRepository
import com.server.ud.dao.post.PostsByPostTypeTrackerRepository
import com.server.ud.dto.CommunityWallFeedRequest
import com.server.ud.dto.CommunityWallViewResponse
import com.server.ud.dto.toSavedPostResponse
import com.server.ud.entities.post.*
import com.server.ud.enums.PostType
import com.server.ud.enums.ProcessingType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.cache.BlockedIDs
import com.server.ud.provider.cache.UDCacheProviderV2
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
class PostsByPostTypeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByPostTypeRepository: PostsByPostTypeRepository

    @Autowired
    private lateinit var postsByPostTypeTrackerRepository: PostsByPostTypeTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var udCacheProvider: UDCacheProviderV2

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun save(post: Post): PostsByPostType? {
        logger.error("Saving PostsByPostType for ${post.postId}.")
        return try {
            val postsByPostType = PostsByPostType(
                postType = post.postType,
                createdAt = post.createdAt,
                postId = post.postId,
                userId = post.userId,
                locationId = post.locationId,
                zipcode = post.zipcode,
                locationName = post.locationName,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                locality = post.locality,
                subLocality = post.subLocality,
                route = post.route,
                city = post.city,
                state = post.state,
                country = post.country,
                countryCode = post.countryCode,
                completeAddress = post.completeAddress,
                title = post.title,
                description = post.description,
                media = post.media,
                sourceMedia = post.sourceMedia,
                tags = post.tags,
                categories = post.categories,
            )
            val savedPost = postsByPostTypeRepository.save(postsByPostType)
            postsByPostTypeTrackerRepository.save(savedPost.toPostsByPostTypeTracker())
            savedPost
        } catch (e: Exception) {
            logger.error("Saving PostsByPostType failed for ${post.postId}.")
            e.printStackTrace()
            null
        }
    }

    fun getCommunityWallViewResponse(request: CommunityWallFeedRequest): CommunityWallViewResponse {
        return try {

            // Step 1: Try to get from cache first

            // Step 2: In case cache has not data then get from db
            getCommunityWallViewResponseFromDB(request)


        } catch (e: Exception) {
            logger.error("Error while getting getCommunityWallViewResponse data from cache.")
            e.printStackTrace()
            CommunityWallViewResponse(
                posts = emptyList(),
                count = 0,
                hasNext = true,
                pagingState = request.pagingState
            )
        }
    }

    private fun getCommunityWallViewResponseFromDB(request: CommunityWallFeedRequest): CommunityWallViewResponse {
        val result = getFeedForForumFeedType(request)
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

    private fun getFeedForForumFeedType(request: CommunityWallFeedRequest): CassandraPageV2<PostsByPostType> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
         val posts = postsByPostTypeRepository.findAllByPostType(PostType.COMMUNITY_WALL_POST, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun deletePostExpandedData(post: Post) {
        deletePostExpandedDataWithPostId(post.postId)
    }

    fun getAllPostsTracker(postId: String): List<PostsByPostTypeTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<PostsByPostTypeTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = postsByPostTypeTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        while (hasNext) {
            pagingState = slicedResult.pagingState ?: ""
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = postsByPostTypeTrackerRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            trackedPosts.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedPosts
    }

    fun getAllByPostId(postId: String) : List<PostsByPostType> {
        val trackedPosts = getAllPostsTracker(postId)
        val posts = mutableListOf<PostsByPostType>()
        return trackedPosts.map {
            it.toPostsByPostType()
        }
//        return posts
    }

    fun deletePostExpandedDataWithPostId(postId: String) {
        val posts = getAllByPostId(postId)
        posts.chunked(10).map {
            postsByPostTypeRepository.deleteAll(it)
        }
    }

    fun processPostExpandedData(post: Post) {
        runBlocking {
            save(post)
        }
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            when (processingType) {
                ProcessingType.NO_PROCESSING -> logger.error("This should not happen. Updating a category without processing should never happen.")
                ProcessingType.DELETE_AND_REFRESH -> {
                    // Delete old data
                    deletePostExpandedData(postUpdate.oldPost)
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
}
