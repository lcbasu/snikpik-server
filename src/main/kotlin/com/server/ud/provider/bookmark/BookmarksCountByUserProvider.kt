package com.server.ud.provider.bookmark

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.bookmark.BookmarksCountByUserRepository
import com.server.ud.entities.bookmark.BookmarksCountByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            resourceBookmarks.getOrElse(0) {
                val bookmarksCountByUser = BookmarksCountByUser()
                bookmarksCountByUser.bookmarksCount = 0
                bookmarksCountByUser.userId = userId
                bookmarksCountByUser
            }
        } catch (e: Exception) {
            logger.error("Getting BookmarksCountByUser for $userId failed.")
            e.printStackTrace()
            null
        }

    fun increaseBookmark(userId: String) {
        bookmarksCountByUserRepository.incrementBookmarkCount(userId)
        logger.info("Increased bookmark for userId: $userId")
        saveBookmarksCountByUserToFirestore(getBookmarksCountByUser(userId))
    }
    fun decreaseBookmark(userId: String) {
        val existing = getBookmarksCountByUser(userId)
        if (existing?.bookmarksCount != null && existing.bookmarksCount!! > 0) {
            bookmarksCountByUserRepository.decrementBookmarkCount(userId)
            logger.info("Decreased bookmark for userId: $userId")
        } else {
            logger.warn("The bookmarks count is already zero. So skipping decreasing it further for userId: $userId")
        }
        saveBookmarksCountByUserToFirestore(getBookmarksCountByUser(userId))
    }

    private fun saveBookmarksCountByUserToFirestore (bookmarksCountByUser: BookmarksCountByUser?) {
        GlobalScope.launch {
            if (bookmarksCountByUser?.userId == null) {
                logger.error("No user id found in bookmarksCountByUser. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("users")
                .document(bookmarksCountByUser.userId!!)
                .collection("bookmarks_count_by_user")
                .document(bookmarksCountByUser.userId!!)
                .set(bookmarksCountByUser)
        }
    }

    fun saveAllToFirestore() {
        bookmarksCountByUserRepository.findAll().forEach {
            saveBookmarksCountByUserToFirestore(it)
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {

        }
    }

}
