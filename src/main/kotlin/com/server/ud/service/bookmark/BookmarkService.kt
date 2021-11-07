package com.server.ud.service.bookmark

import com.server.ud.dto.BookmarkReportDetail
import com.server.ud.dto.SaveBookmarkRequest
import com.server.ud.entities.bookmark.Bookmark

abstract class BookmarkService {
    abstract fun saveBookmark(request: SaveBookmarkRequest): Bookmark
    abstract fun getBookmarkReportDetail(resourceId: String): BookmarkReportDetail
}
