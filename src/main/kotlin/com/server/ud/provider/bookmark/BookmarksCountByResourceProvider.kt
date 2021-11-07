package com.server.ud.provider.bookmark

import com.server.ud.dao.bookmark.BookmarksCountByResourceRepository
import com.server.ud.entities.bookmark.BookmarksCountByResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BookmarksCountByResourceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarksCountByResourceRepository: BookmarksCountByResourceRepository

    fun getBookmarksCountByResource(resourceId: String): BookmarksCountByResource? =
        try {
            val resourceBookmarks = bookmarksCountByResourceRepository.findAllByResourceId(resourceId)
            if (resourceBookmarks.size > 1) {
                error("More than one bookmarks has same resourceId: $resourceId")
            }
            resourceBookmarks.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting BookmarksCountByResource for $resourceId failed.")
            e.printStackTrace()
            null
        }

    fun increaseBookmark(resourceId: String) {
        bookmarksCountByResourceRepository.incrementBookmarkCount(resourceId)
        logger.warn("Increased bookmark for resourceId: $resourceId")
    }
    fun decreaseBookmark(resourceId: String) {
        val existing = getBookmarksCountByResource(resourceId)
        if (existing?.bookmarksCount != null && existing.bookmarksCount!! > 0) {
            bookmarksCountByResourceRepository.decrementBookmarkCount(resourceId)
            logger.warn("Decreased bookmark for resourceId: $resourceId")
        } else {
            logger.warn("The bookmarks count is already zero. So skipping decreasing it further for resourceId: $resourceId")
        }
    }

}
