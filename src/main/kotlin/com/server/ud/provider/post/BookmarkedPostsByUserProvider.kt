package com.server.ud.provider.post

import com.server.ud.dao.post.BookmarkedPostsByUserRepository
import com.server.ud.dto.*
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.post.BookmarkedPostsByUser
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostUpdate
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
                    bookmarkedPostsByUserRepository.deleteByUserIdAndPostTypeAndPostId(
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
                zipcode = post.zipcode!!,
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
            return bookmarkedPostsByUserRepository.save(bookmarkedPostByUser)
        } catch (e: Exception) {
            logger.error("Saving BookmarkedPostsByUser failed for bookmarkId: ${bookmark.bookmarkId}.")
            e.printStackTrace()
            return null
        }
    }

    fun update(bookmarkedPostsByUser: BookmarkedPostsByUser, updatedPost: Post): BookmarkedPostsByUser? {
        try {
            return bookmarkedPostsByUserRepository.save(bookmarkedPostsByUser.copy(
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
                zipcode = updatedPost.zipcode!!,
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

    fun getBookmarkedPostsByUser(request: BookmarkedPostsByUserRequest): CassandraPageV2<BookmarkedPostsByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
         val posts = bookmarkedPostsByUserRepository.findAllByUserId(request.userId, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun getAllByPostId(postId: String) = bookmarkedPostsByUserRepository.findAllByPostId(postId)

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            val all = getAllByPostId(postId)
            all.chunked(5).forEach {
                bookmarkedPostsByUserRepository.deleteAll(it)
            }
        }
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

}
