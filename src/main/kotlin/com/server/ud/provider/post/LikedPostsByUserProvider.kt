package com.server.ud.provider.post

import com.server.ud.dao.post.LikedPostsByUserRepository
import com.server.ud.dao.post.LikedPostsByUserTrackerRepository
import com.server.ud.dto.LikedPostsByUserRequest
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.*
import com.server.ud.enums.ProcessingType
import com.server.ud.enums.ResourceType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class LikedPostsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likedPostsByUserRepository: LikedPostsByUserRepository

    @Autowired
    private lateinit var likedPostsByUserTrackerRepository: LikedPostsByUserTrackerRepository

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun processLike(like: Like): LikedPostsByUser? {
        return try {
            if (like.resourceType == ResourceType.POST || like.resourceType == ResourceType.WALL) {
                val post = postProvider.getPost(like.resourceId) ?: error("Error while getting post with postId: ${like.resourceId}")
                if (like.liked) {
                    save(like, post)
                } else {
                    likedPostsByUserRepository.deleteAllByUserIdAndPostTypeAndPostId(
                        userId = like.userId,
                        postType = post.postType,
                        postId = like.resourceId,
                    )
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Saving LikedPostsByUser failed for likeId: ${like.liked}.")
            e.printStackTrace()
            null
        }
    }

    fun save(like: Like, post: Post): LikedPostsByUser? {
        try {
            val likedPostByUser = LikedPostsByUser(
                userId = like.userId,
                createdAt = like.createdAt,
                postCreatedAt = post.createdAt,
                postedByUserId = post.userId,
                postId = post.postId,
                postType = post.postType,
                title = post.title,
                description = post.description,
                media = post.media,
                sourceMedia = post.sourceMedia,
                tags = post.tags,
                categories = post.categories,
            )
            return save(likedPostByUser)
        } catch (e: Exception) {
            logger.error("Saving LikedPostsByUser failed for likeId: ${like.likeId}.")
            e.printStackTrace()
            return null
        }
    }

    fun update(likedPostsByUser: LikedPostsByUser, updatedPost: Post): LikedPostsByUser? {
        try {
            return save(likedPostsByUser.copy(
                postCreatedAt = updatedPost.createdAt,
                postedByUserId = updatedPost.userId,
                postId = updatedPost.postId,
                postType = updatedPost.postType,
                title = updatedPost.title,
                description = updatedPost.description,
                media = updatedPost.media,
                sourceMedia = updatedPost.sourceMedia,
                tags = updatedPost.tags,
                categories = updatedPost.categories,
            ))
        } catch (e: Exception) {
            logger.error("Saving LikedPostsByUser failed for userId: ${updatedPost.userId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getLikedPostsByUser(request: LikedPostsByUserRequest): CassandraPageV2<LikedPostsByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
         val posts = likedPostsByUserRepository.findAllByUserId(request.userId, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun deletePostExpandedData(postId: String) {
        val all = getAllByPostId(postId)
        likedPostsByUserRepository.deleteAll(all)
    }

    fun getAllPostsTracker(postId: String): List<LikedPostsByUserTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<LikedPostsByUserTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = likedPostsByUserTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        while (hasNext) {
            pagingState = slicedResult.pagingState ?: ""
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = likedPostsByUserTrackerRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            trackedPosts.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedPosts
    }

    fun getAllByPostId(postId: String) : List<LikedPostsByUser> {
        val trackedPosts = getAllPostsTracker(postId)
        val posts = mutableListOf<LikedPostsByUser>()
        return trackedPosts.map {
            it.toLikedPostsByUser()
        }
//        return posts
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            val updatedPost = postUpdate.newPost!!
            val all = getAllByPostId(updatedPost.postId)
            all.chunked(5).map {
                async { it.map { update(it, updatedPost) } }
            }.map {
                it.await()
            }
        }
    }
    private fun save(likedPostsByUser: LikedPostsByUser): LikedPostsByUser {
        val savedData = likedPostsByUserRepository.save(likedPostsByUser)
        likedPostsByUserTrackerRepository.save(savedData.toLikedPostsByUserTracker())
        return savedData
    }
}
