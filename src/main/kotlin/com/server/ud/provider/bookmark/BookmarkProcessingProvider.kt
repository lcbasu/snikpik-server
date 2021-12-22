package com.server.ud.provider.bookmark

import com.server.ud.dao.bookmark.*
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.provider.post.BookmarkedPostsByUserProvider
import com.server.ud.provider.user_activity.UserActivitiesProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    @Autowired
    private lateinit var bookmarksForResourceByUserRepository: BookmarkForResourceByUserRepository

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    private lateinit var bookmarksByResourceRepository: BookmarksByResourceRepository

    @Autowired
    private lateinit var bookmarksByUserRepository: BookmarksByUserRepository

    @Autowired
    private lateinit var bookmarksCountByResourceRepository: BookmarksCountByResourceRepository

    @Autowired
    private lateinit var bookmarksCountByUserRepository: BookmarksCountByUserRepository

    @Autowired
    private lateinit var userActivitiesProvider: UserActivitiesProvider

    fun processBookmark(bookmarkId: String) {
        GlobalScope.launch {
            logger.info("Later:Start: bookmark processing for bookmarkId: $bookmarkId")
            val bookmark = bookmarksProvider.getBookmark(bookmarkId) ?: error("Failed to get bookmark data for bookmarkId: $bookmarkId")
            val userActivityFuture = async {
                userActivitiesProvider.saveBookmarkLevelActivity(bookmark)
            }
            val bookmarkedPostsByUserProviderFuture = async { bookmarkedPostsByUserProvider.processBookmark(bookmark) }
            val bookmarksByResourceFuture = async { bookmarksByResourceProvider.save(bookmark) }
            val bookmarksByUserFuture = async { bookmarksByUserProvider.save(bookmark) }

            bookmarksByResourceFuture.await()
            bookmarksByUserFuture.await()
            bookmarkedPostsByUserProviderFuture.await()
            userActivityFuture.await()
            logger.info("Later:Done: bookmark processing for bookmarkId: $bookmarkId")
        }
    }

    fun thingsToDoForBookmarkProcessingNow(bookmark: Bookmark) {
        runBlocking {
            logger.info("Now:Start: bookmark processing for bookmarkId: ${bookmark.bookmarkId}")
            val bookmarked = bookmarkForResourceByUserProvider.getBookmarkForResourceByUser(
                resourceId = bookmark.resourceId,
                userId = bookmark.userId
            )?.bookmarked ?: false
            if (bookmarked != bookmark.bookmarked) {
                val bookmarkForResourceByUserFuture = async { bookmarkForResourceByUserProvider.setBookmark(bookmark.resourceId, bookmark.userId, bookmark.bookmarked) }
                val bookmarksCountByUserFuture = async { if (bookmark.bookmarked) bookmarksCountByUserProvider.increaseBookmark(bookmark.userId) else bookmarksCountByUserProvider.decreaseBookmark(bookmark.userId) }
                val bookmarksCountByResourceFuture = async { if (bookmark.bookmarked) bookmarksCountByResourceProvider.increaseBookmark(bookmark.resourceId) else bookmarksCountByResourceProvider.decreaseBookmark(bookmark.resourceId) }
                bookmarksCountByResourceFuture.await()
                bookmarkForResourceByUserFuture.await()
                bookmarksCountByUserFuture.await()
            }
            logger.info("Now:Done: bookmark processing for bookmarkId: ${bookmark.bookmarkId}")
        }
    }

    fun deletePost(postId: String) {
        GlobalScope.launch {
            val allUsersBookmarks = bookmarksByUserRepository.findAllByResourceId(postId)
            val bookmarksGroupedByUser = allUsersBookmarks.groupBy { it.userId }
            bookmarksGroupedByUser.map {
                val userId = it.key
                val userBookmarks = it.value
                val bookmarkedRows = userBookmarks.filter { it.bookmarked }
                val unBookmarkedRows = userBookmarks.filter { !it.bookmarked }
                // Decrease like only if the post has been bookmarked in the end
                if (bookmarkedRows.size > unBookmarkedRows.size) {
                    bookmarksCountByUserRepository.decrementBookmarkCount(userId)
                }
            }
            bookmarksForResourceByUserRepository.deleteAll(bookmarksForResourceByUserRepository.findAllByResourceId(postId))
            bookmarkRepository.deleteAll(bookmarkRepository.findAllByResourceId(postId))
            bookmarksByResourceRepository.deleteAll(bookmarksByResourceRepository.findAllByResourceId(postId))
            bookmarksCountByResourceRepository.deleteAll(bookmarksCountByResourceRepository.findAllByResourceId(postId))
            bookmarksByUserRepository.deleteAll(allUsersBookmarks)
        }
    }

}
