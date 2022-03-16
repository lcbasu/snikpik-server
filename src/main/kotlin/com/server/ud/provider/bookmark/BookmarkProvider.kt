package com.server.ud.provider.bookmark

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.ud.dao.bookmark.*
import com.server.ud.dto.SaveBookmarkRequest
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.enums.BookmarkUpdateAction
import com.server.ud.provider.job.UDJobProvider
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
class BookmarkProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var udJobProvider: UDJobProvider

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
    private lateinit var bookmarksByResourceRepository: BookmarksByResourceRepository

    @Autowired
    private lateinit var bookmarksByUserRepository: BookmarksByUserRepository

    @Autowired
    private lateinit var bookmarksCountByResourceRepository: BookmarksCountByResourceRepository

    @Autowired
    private lateinit var bookmarksCountByUserRepository: BookmarksCountByUserRepository

    @Autowired
    private lateinit var userActivitiesProvider: UserActivitiesProvider

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
                bookmarkId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.BMK.name),
                userId = userId,
                resourceId = request.resourceId,
                resourceType = request.resourceType,
                bookmarked = request.action == BookmarkUpdateAction.ADD
            )
            val savedBookmark = bookmarkRepository.save(bookmark)
            thingsToDoForBookmarkProcessingNow(savedBookmark)
            udJobProvider.scheduleProcessingForBookmark(savedBookmark.bookmarkId)
            return savedBookmark
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun processBookmark(bookmarkId: String) {
        GlobalScope.launch {
            logger.info("Later:Start: bookmark processing for bookmarkId: $bookmarkId")
            val bookmark = bookmarksProvider.getBookmark(bookmarkId) ?: error("Failed to get bookmark data for bookmarkId: $bookmarkId")
            val userActivityFuture = async {
                if (bookmark.bookmarked) {
                    userActivitiesProvider.saveBookmarkLevelActivity(bookmark)
                } else {
                    userActivitiesProvider.deleteBookmarkLevelActivity(bookmark)
                }
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

    fun deleteResourceExpandedData(resourceId: String) {
        GlobalScope.launch {
            val bookmarksByResource = bookmarksByResourceRepository.findAllByResourceId(resourceId)
            val userIds = bookmarksByResource.map { it.userId }.toSet()
            val bookmarkIds = bookmarksByResource.map { it.bookmarkId }.toSet()
            bookmarkIds.map {
                async { bookmarkRepository.deleteByBookmarkId(it) }
            }.map {
                it.await()
            }

            userIds.map {
                async {
                    val bookmarked = bookmarkForResourceByUserProvider.getBookmarkForResourceByUser(
                        resourceId = resourceId,
                        userId = it
                    )?.bookmarked ?: false

                    if (bookmarked) {
                        bookmarksCountByUserRepository.decrementBookmarkCount(it)
                    }
                    bookmarksForResourceByUserRepository.deleteAllByResourceIdAndUserId(resourceId, it)

                    // TODO: Optimize this
                    val allBookmarksByThisUserForAllPosts = bookmarksByUserRepository.findAllByUserId(it)
                    val allBookmarksByThisUserForTHISPosts = allBookmarksByThisUserForAllPosts.filter { it.resourceId == resourceId }
                    bookmarksByUserRepository.deleteAll(allBookmarksByThisUserForTHISPosts)
                }
            }.map {
                it.await()
            }

            bookmarksByResourceRepository.deleteAllByResourceId(resourceId)
            bookmarksCountByResourceRepository.deleteAllByResourceId(resourceId)
        }
    }

}
