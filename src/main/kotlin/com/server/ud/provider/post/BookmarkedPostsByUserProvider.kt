package com.server.ud.provider.post

import com.server.ud.dao.post.BookmarkedPostsByUserRepository
import com.server.ud.dao.post.BookmarkedPostsByUserTrackerRepository
import com.server.ud.dto.BookmarkedPostsByUserRequest
import com.server.ud.dto.BookmarkedPostsByUserResponse
import com.server.ud.dto.BookmarkedPostsByUserResponseV2
import com.server.ud.entities.bookmark.Bookmark
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
class BookmarkedPostsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarkedPostsByUserRepository: BookmarkedPostsByUserRepository

    @Autowired
    private lateinit var bookmarkedPostsByUserTrackerRepository: BookmarkedPostsByUserTrackerRepository

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun processBookmark(bookmark: Bookmark): BookmarkedPostsByUser? {
        return try {
            if (bookmark.resourceType == ResourceType.POST || bookmark.resourceType == ResourceType.WALL) {
                val post = postProvider.getPost(bookmark.resourceId) ?: error("Error while getting post with postId: ${bookmark.resourceId}")
                if (bookmark.bookmarked) {
                    save(bookmark, post)
                } else {
                    bookmarkedPostsByUserRepository.deleteAllByUserIdAndPostTypeAndPostId(
                        userId = bookmark.userId,
                        postType = post.postType,
                        postId = bookmark.resourceId,
                    )
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Saving BookmarkedPostsByUser failed for bookmarkId: ${bookmark.bookmarkId}.")
            e.printStackTrace()
            null
        }
    }

    fun save(bookmark: Bookmark, post: Post): BookmarkedPostsByUser? {
        try {
            val bookmarkedPostByUser = BookmarkedPostsByUser(
                userId = bookmark.userId,
                createdAt = bookmark.createdAt,
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
            )
            return save(bookmarkedPostByUser)
        } catch (e: Exception) {
            logger.error("Saving BookmarkedPostsByUser failed for bookmarkId: ${bookmark.bookmarkId}.")
            e.printStackTrace()
            return null
        }
    }

    fun update(bookmarkedPostsByUser: BookmarkedPostsByUser, updatedPost: Post): BookmarkedPostsByUser? {
        try {
            return save(bookmarkedPostsByUser.copy(
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

                locationId = updatedPost.locationId,
                zipcode = updatedPost.zipcode,
                locationName = updatedPost.locationName,
                locationLat = updatedPost.locationLat,
                locationLng = updatedPost.locationLng,
                locality = updatedPost.locality,
                subLocality = updatedPost.subLocality,
                route = updatedPost.route,
                city = updatedPost.city,
                state = updatedPost.state,
                country = updatedPost.country,
                countryCode = updatedPost.countryCode,
                completeAddress = updatedPost.completeAddress,
            ))
        } catch (e: Exception) {
            logger.error("Updating BookmarkedPostsByUser failed for userId: ${bookmarkedPostsByUser.userId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getAllPostsTracker(postId: String): List<BookmarkedPostsByUserTracker> {
        val limit = 10
        var pagingState = ""

        val trackedPosts = mutableListOf<BookmarkedPostsByUserTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = bookmarkedPostsByUserTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedPosts.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        while (hasNext) {
            pagingState = slicedResult.pagingState ?: ""
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = bookmarkedPostsByUserTrackerRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            trackedPosts.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedPosts
    }

    fun getBookmarkedPostsByUser(request: BookmarkedPostsByUserRequest): CassandraPageV2<BookmarkedPostsByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
         val posts = bookmarkedPostsByUserRepository.findAllByUserId(request.userId, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun getAllByPostId(postId: String) : List<BookmarkedPostsByUser> {
        val trackedPosts = getAllPostsTracker(postId)
        return trackedPosts.map {
            it.toBookmarkedPostsByUser()
        }
    }

    fun deletePostExpandedData(postId: String) {
        val all = getAllByPostId(postId)
        bookmarkedPostsByUserRepository.deleteAll(all)
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

    fun getBookmarkedPostsByUserResponse(request: BookmarkedPostsByUserRequest): BookmarkedPostsByUserResponse {
        val result = getBookmarkedPostsByUser(request)
        return BookmarkedPostsByUserResponse(
            posts = result.content?.filterNotNull()?.map { it.toBookmarkedPostsByUserPostDetail() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    fun getBookmarkedPostsByUserResponseV2(request: BookmarkedPostsByUserRequest): BookmarkedPostsByUserResponseV2 {
        val result = getBookmarkedPostsByUser(request)
        return BookmarkedPostsByUserResponseV2(
            posts = result.content?.filterNotNull()?.map { it.toSavedPostResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    private fun save(bookmarkedPostsByUser: BookmarkedPostsByUser): BookmarkedPostsByUser {
        val savedData = bookmarkedPostsByUserRepository.save(bookmarkedPostsByUser)
        bookmarkedPostsByUserTrackerRepository.save(savedData.toBookmarkedPostsByUserTracker())
        return savedData
    }

}
