package com.server.ud.provider.bookmark

import com.server.common.utils.DateUtils
import com.server.ud.dao.bookmark.BookmarksByResourceRepository
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.bookmark.BookmarksByResource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BookmarksByResourceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarksByResourceRepository: BookmarksByResourceRepository

    fun save(bookmark: Bookmark) : BookmarksByResource? {
        try {
            val bookmarksByResource = BookmarksByResource(
                resourceId = bookmark.resourceId,
                resourceType = bookmark.resourceType,
                createdAt = bookmark.createdAt,
                userId = bookmark.userId,
                bookmarkId = bookmark.bookmarkId,
                bookmarked = bookmark.bookmarked,
            )
            val savedBookmarksByResource = bookmarksByResourceRepository.save(bookmarksByResource)
            logger.info("Saved BookmarksByResource into cassandra for bookmarkId: ${bookmark.bookmarkId}")
            return savedBookmarksByResource
        } catch (e: Exception) {
            logger.error("Saving BookmarksByResource into cassandra failed for bookmarkId: ${bookmark.bookmarkId}")
            e.printStackTrace()
            return null
        }
    }
}
