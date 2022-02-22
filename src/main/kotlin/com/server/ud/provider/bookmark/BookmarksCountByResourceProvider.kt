package com.server.ud.provider.bookmark

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.bookmark.BookmarksCountByResourceRepository
import com.server.ud.entities.bookmark.BookmarksCountByResource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            resourceBookmarks.getOrElse(0) {
                val bookmarksCountByResource = BookmarksCountByResource()
                bookmarksCountByResource.bookmarksCount = 0
                bookmarksCountByResource.resourceId = resourceId
                bookmarksCountByResource
            }
        } catch (e: Exception) {
            logger.error("Getting BookmarksCountByResource for $resourceId failed.")
            e.printStackTrace()
            null
        }

    fun increaseBookmark(resourceId: String) {
        bookmarksCountByResourceRepository.incrementBookmarkCount(resourceId)
        logger.warn("Increased bookmark for resourceId: $resourceId")
        saveBookmarksCountByResourceToFirestore(getBookmarksCountByResource(resourceId))
    }
    fun decreaseBookmark(resourceId: String) {
        val existing = getBookmarksCountByResource(resourceId)
        if (existing?.bookmarksCount != null && existing.bookmarksCount!! > 0) {
            bookmarksCountByResourceRepository.decrementBookmarkCount(resourceId)
            logger.warn("Decreased bookmark for resourceId: $resourceId")
        } else {
            logger.warn("The bookmarks count is already zero. So skipping decreasing it further for resourceId: $resourceId")
        }
        saveBookmarksCountByResourceToFirestore(getBookmarksCountByResource(resourceId))
    }

    private fun saveBookmarksCountByResourceToFirestore (bookmarksCountByResource: BookmarksCountByResource?) {
        GlobalScope.launch {
            if (bookmarksCountByResource?.resourceId == null) {
                logger.error("No resource id found in bookmarksCountByResource. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("bookmarks_count_by_resource")
                .document(bookmarksCountByResource.resourceId!!)
                .set(bookmarksCountByResource)
        }
    }

    fun saveAllToFirestore() {
        bookmarksCountByResourceRepository.findAll().forEach {
            saveBookmarksCountByResourceToFirestore(it)
        }
    }


    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {

        }
    }
}
