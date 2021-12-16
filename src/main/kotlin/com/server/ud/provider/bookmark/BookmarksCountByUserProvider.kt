package com.server.ud.provider.bookmark

import com.server.ud.dao.bookmark.BookmarksCountByUserRepository
import com.server.ud.entities.bookmark.BookmarksCountByUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BookmarksCountByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarksCountByUserRepository: BookmarksCountByUserRepository

    fun getBookmarksCountByUser(userId: String): BookmarksCountByUser? =
        try {
            val resourceBookmarks = bookmarksCountByUserRepository.findAllByUserId(userId)
            if (resourceBookmarks.size > 1) {
                error("More than one bookmarks has same userId: $userId")
            }
            resourceBookmarks.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting BookmarksCountByUser for $userId failed.")
            e.printStackTrace()
            null
        }

    fun increaseBookmark(userId: String) {
        bookmarksCountByUserRepository.incrementBookmarkCount(userId)
        logger.warn("Increased bookmark for userId: $userId")
    }
    fun decreaseBookmark(userId: String) {
        val existing = getBookmarksCountByUser(userId)
        if (existing?.bookmarksCount != null && existing.bookmarksCount!! > 0) {
            bookmarksCountByUserRepository.decrementBookmarkCount(userId)
            logger.warn("Decreased bookmark for userId: $userId")
        } else {
            logger.warn("The bookmarks count is already zero. So skipping decreasing it further for userId: $userId")
        }
    }

}
