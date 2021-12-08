package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.DateUtils
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.enums.BookmarkUpdateAction
import com.server.ud.enums.ResourceType
import com.server.ud.provider.bookmark.BookmarksCountByResourceProvider

data class BookmarkReportDetailForUser(
    val userId: String,
    val bookmarked: Boolean
)

data class BookmarkReportDetail(
    val resourceId: String,
    val bookmarks: Long,
    val userLevelInfo: BookmarkReportDetailForUser
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveBookmarkRequest(
    var resourceType: ResourceType,
    var resourceId: String,
    var action: BookmarkUpdateAction
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedBookmarkResponse(
    val bookmarkId: String,
    val createdAt: Long,
    val resourceId: String,
    val resourceType: ResourceType,
    val userId: String,
    val bookmarked: Boolean,
    val totalBookmarks: Long,
)

fun Bookmark.toSavedBookmarkResponse(provider: BookmarksCountByResourceProvider): SavedBookmarkResponse {
    this.apply {
        return SavedBookmarkResponse(
            bookmarkId = bookmarkId,
            createdAt = DateUtils.getEpoch(createdAt),
            resourceId = resourceId,
            resourceType = resourceType,
            userId = userId,
            bookmarked = bookmarked,
            totalBookmarks = provider.getBookmarksCountByResource(resourceId)?.bookmarksCount ?: 0,
        )
    }
}
