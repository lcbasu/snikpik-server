package com.server.ud.provider.bookmark

import com.server.ud.provider.post.BookmarkedPostsByUserProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BookmarkProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarksProvider: BookmarkProvider

    @Autowired
    private lateinit var bookmarksByResourceProvider: BookmarksByResourceProvider

    @Autowired
    private lateinit var bookmarksByUserProvider: BookmarksByUserProvider

    @Autowired
    private lateinit var bookmarksCountByResourceProvider: BookmarksCountByResourceProvider

    @Autowired
    private lateinit var bookmarkForResourceByUserProvider: BookmarkForResourceByUserProvider

    @Autowired
    private lateinit var bookmarksCountByUserProvider: BookmarksCountByUserProvider

    @Autowired
    private lateinit var bookmarkedPostsByUserProvider: BookmarkedPostsByUserProvider

    fun processBookmark(bookmarkId: String) {
        GlobalScope.launch {
            logger.info("Start: bookmark processing for bookmarkId: $bookmarkId")
            val bookmark = bookmarksProvider.getBookmark(bookmarkId) ?: error("Failed to get bookmark data for bookmarkId: $bookmarkId")
            val bookmarksByResourceFuture = async { bookmarksByResourceProvider.save(bookmark) }
            val bookmarksByUserFuture = async { bookmarksByUserProvider.save(bookmark) }
            val bookmarksCountByResourceFuture = async { if (bookmark.bookmarked) bookmarksCountByResourceProvider.increaseBookmark(bookmark.resourceId) else bookmarksCountByResourceProvider.decreaseBookmark(bookmark.resourceId) }
            val bookmarksCountByResourceAndUserFuture = async { bookmarkForResourceByUserProvider.setBookmark(bookmark.resourceId, bookmark.userId, bookmark.bookmarked) }
            val bookmarksCountByUserFuture = async { if (bookmark.bookmarked) bookmarksCountByUserProvider.increaseBookmark(bookmark.userId) else bookmarksCountByUserProvider.decreaseBookmark(bookmark.userId) }
            val bookmarkedPostsByUserProviderFuture = async { bookmarkedPostsByUserProvider.processBookmark(bookmark) }
            bookmarksByResourceFuture.await()
            bookmarksByUserFuture.await()
            bookmarksCountByResourceFuture.await()
            bookmarksCountByResourceAndUserFuture.await()
            bookmarksCountByUserFuture.await()
            bookmarkedPostsByUserProviderFuture.await()
            logger.info("Done: bookmark processing for bookmarkId: $bookmarkId")
        }
    }


}
