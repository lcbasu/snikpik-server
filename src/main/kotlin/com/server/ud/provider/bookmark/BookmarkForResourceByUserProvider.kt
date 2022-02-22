package com.server.ud.provider.bookmark

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.bookmark.BookmarkForResourceByUserRepository
import com.server.ud.entities.bookmark.BookmarkForResourceByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BookmarkForResourceByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarksForResourceByUserRepository: BookmarkForResourceByUserRepository

    fun getBookmarkForResourceByUser(resourceId: String, userId: String): BookmarkForResourceByUser? =
    try {
        val resourceBookmarks = bookmarksForResourceByUserRepository.findAllByResourceIdAndUserId(resourceId, userId)
        if (resourceBookmarks.size > 1) {
            error("More than one bookmarks has same resourceId: $resourceId by the userId: $userId")
        }
        resourceBookmarks.firstOrNull()
    } catch (e: Exception) {
        logger.error("Getting BookmarkForResourceByUser for $resourceId & userId: $userId failed.")
        e.printStackTrace()
        null
    }

    fun save(resourceId: String, userId: String, bookmarked: Boolean) : BookmarkForResourceByUser? {
        return try {
            val bookmarks = BookmarkForResourceByUser(
                resourceId = resourceId,
                userId = userId,
                bookmarked = bookmarked,
            )
            val result = bookmarksForResourceByUserRepository.save(bookmarks)
            saveBookmarkForResourceByUserToFirestore(result)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun setBookmark(resourceId: String, userId: String, value: Boolean) {
        save(resourceId, userId, value)
    }

    private fun saveBookmarkForResourceByUserToFirestore (bookmarkForResourceByUser: BookmarkForResourceByUser) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("users")
                .document(bookmarkForResourceByUser.userId)
                .collection("bookmark_for_resource_by_user")
                .document(bookmarkForResourceByUser.resourceId)
                .set(bookmarkForResourceByUser)
        }
    }

    fun saveAllToFirestore() {
        bookmarksForResourceByUserRepository.findAll().forEach {
            saveBookmarkForResourceByUserToFirestore(it!!)
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {

        }
    }
}
