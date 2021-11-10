package com.server.ud.provider.bookmark

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.ud.dao.bookmark.BookmarkRepository
import com.server.ud.dto.SaveBookmarkRequest
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.BookmarkUpdateAction
import com.server.ud.service.bookmark.ProcessBookmarkSchedulerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BookmarkProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var processBookmarkSchedulerService: ProcessBookmarkSchedulerService

    fun getBookmark(bookmarkId: String): Bookmark? =
        try {
            val bookmarks = bookmarkRepository.findAllByBookmarkId(bookmarkId)
            if (bookmarks.size > 1) {
                error("More than one bookmark has same bookmarkId: $bookmarkId")
            }
            bookmarks.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting Bookmark for $bookmarkId failed.")
            e.printStackTrace()
            null
        }

    fun save(user: UserV2, request: SaveBookmarkRequest) : Bookmark? {
        try {
            val bookmark = Bookmark(
                bookmarkId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.BMK.name),
                userId = user.userId,
                resourceId = request.resourceId,
                resourceType = request.resourceType,
                bookmarked = request.action == BookmarkUpdateAction.ADD
            )
            val savedBookmark = bookmarkRepository.save(bookmark)
            processBookmarkSchedulerService.createBookmarkProcessingJob(savedBookmark)
            return savedBookmark
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}