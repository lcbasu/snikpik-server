package com.server.ud.service.bookmark

import com.server.ud.entities.bookmark.Bookmark

abstract class ProcessBookmarkSchedulerService {
    abstract fun createBookmarkProcessingJob(bookmark: Bookmark): Bookmark
}
