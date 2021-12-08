package com.server.ud.provider.post

import com.server.ud.dao.post.BookmarkedPostsByUserRepository
import com.server.ud.dto.BookmarkedPostsByUserRequest
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.post.BookmarkedPostsByUser
import com.server.ud.entities.post.Post
import com.server.ud.enums.ResourceType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
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
                tags = post.tags,
                categories = post.categories,
            )
            return bookmarkedPostsByUserRepository.save(bookmarkedPostByUser)
        } catch (e: Exception) {
            logger.error("Saving BookmarkedPostsByUser failed for bookmarkId: ${bookmark.bookmarkId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getBookmarkedPostsByUser(request: BookmarkedPostsByUserRequest): CassandraPageV2<BookmarkedPostsByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
         val posts = bookmarkedPostsByUserRepository.findAllByUserId(request.userId, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

}
