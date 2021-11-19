package com.server.ud.service.bookmark

import com.server.ud.dto.BookmarkReportDetail
import com.server.ud.dto.SaveBookmarkRequest
import com.server.ud.dto.SavedBookmarkResponse

abstract class BookmarkService {
    abstract fun saveBookmark(request: SaveBookmarkRequest): SavedBookmarkResponse?
    abstract fun getBookmarkReportDetail(resourceId: String): BookmarkReportDetail
}
