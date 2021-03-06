package com.server.ud.controller

import com.server.ud.dto.BookmarkReportDetail
import com.server.ud.dto.SaveBookmarkRequest
import com.server.ud.dto.SavedBookmarkResponse
import com.server.ud.service.bookmark.BookmarkService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/bookmark")
class BookmarkController {

    @Autowired
    private lateinit var bookmarkService: BookmarkService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveBookmark(@RequestBody request: SaveBookmarkRequest): SavedBookmarkResponse? {
        return bookmarkService.saveBookmark(request)
    }

    @RequestMapping(value = ["/getBookmarkReportDetail"], method = [RequestMethod.GET])
    fun getBookmarkReportDetail(@RequestParam resourceId: String): BookmarkReportDetail {
        return bookmarkService.getBookmarkReportDetail(resourceId)
    }
}
