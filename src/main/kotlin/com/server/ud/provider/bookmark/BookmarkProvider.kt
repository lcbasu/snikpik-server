package com.server.ud.provider.bookmark

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.RandomIdProvider
import com.server.ud.dao.bookmark.BookmarkRepository
import com.server.ud.dto.SaveBookmarkRequest
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.enums.BookmarkUpdateAction
import com.server.ud.provider.deferred.DeferredProcessingProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private lateinit var randomIdProvider: RandomIdProvider

    @Autowired
    private lateinit var deferredProcessingProvider: DeferredProcessingProvider

    @Autowired
    private lateinit var bookmarkProcessingProvider: BookmarkProcessingProvider

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

    fun save(userId: String, request: SaveBookmarkRequest) : Bookmark? {
        try {
            val bookmark = Bookmark(
                bookmarkId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.BMK),
                userId = userId,
                resourceId = request.resourceId,
                resourceType = request.resourceType,
                bookmarked = request.action == BookmarkUpdateAction.ADD
            )
            val savedBookmark = bookmarkRepository.save(bookmark)
            bookmarkProcessingProvider.thingsToDoForBookmarkProcessingNow(savedBookmark)
            deferredProcessingProvider.deferProcessingForBookmark(savedBookmark.bookmarkId)
            return savedBookmark
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
