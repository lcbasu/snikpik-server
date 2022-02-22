package com.server.ud.provider.bookmark

import com.server.common.utils.DateUtils
import com.server.ud.dao.bookmark.BookmarksByUserRepository
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.bookmark.BookmarksByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BookmarksByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarksByUserRepository: BookmarksByUserRepository

    fun save(bookmark: Bookmark) : BookmarksByUser? {
        try {
            val bookmarksByUser = BookmarksByUser(
                userId = bookmark.userId,
                createdAt = bookmark.createdAt,
                resourceId = bookmark.resourceId,
                resourceType = bookmark.resourceType,
                bookmarkId = bookmark.bookmarkId,
                bookmarked = bookmark.bookmarked,
            )
            val savedBookmarksByUser = bookmarksByUserRepository.save(bookmarksByUser)
            logger.info("Saved BookmarksByUser into cassandra for bookmarkId: ${bookmark.bookmarkId}")
            return savedBookmarksByUser
        } catch (e: Exception) {
            logger.error("Saving BookmarksByUser into cassandra failed for bookmarkId: ${bookmark.bookmarkId}")
            e.printStackTrace()
            return null
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {

        }
    }
}
