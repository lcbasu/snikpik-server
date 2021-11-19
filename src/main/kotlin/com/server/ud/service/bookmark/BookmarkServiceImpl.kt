package com.server.ud.service.bookmark

import com.server.common.provider.AuthProvider
import com.server.ud.dto.*
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.provider.bookmark.BookmarkForResourceByUserProvider
import com.server.ud.provider.bookmark.BookmarkProvider
import com.server.ud.provider.bookmark.BookmarksCountByResourceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BookmarkServiceImpl : BookmarkService() {


    @Autowired
    private lateinit var bookmarkProvider: BookmarkProvider

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var bookmarksCountByResourceProvider: BookmarksCountByResourceProvider

    @Autowired
    private lateinit var bookmarkForResourceByUserProvider: BookmarkForResourceByUserProvider

    override fun saveBookmark(request: SaveBookmarkRequest): SavedBookmarkResponse? {
        val requestContext = authProvider.validateRequest()
        return bookmarkProvider.save(requestContext.userV2, request)?.toSavedBookmarkResponse()
    }

    override fun getBookmarkReportDetail(resourceId: String): BookmarkReportDetail {
        val requestContext = authProvider.validateRequest()
        val bookmarksCountByResource = bookmarksCountByResourceProvider.getBookmarksCountByResource(resourceId)?.bookmarksCount ?: 0
        val bookmarked = bookmarkForResourceByUserProvider.getBookmarkForResourceByUser(
            resourceId = resourceId,
            userId = requestContext.userV2.userId
        )?.bookmarked ?: false
        return BookmarkReportDetail(
            resourceId = resourceId,
            bookmarks = bookmarksCountByResource,
            userLevelInfo = BookmarkReportDetailForUser(
                userId = requestContext.userV2.userId,
                bookmarked = bookmarked
            )
        )
    }
}
