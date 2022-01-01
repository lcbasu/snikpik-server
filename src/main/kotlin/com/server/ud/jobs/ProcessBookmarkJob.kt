package com.server.ud.jobs

import com.server.ud.provider.bookmark.BookmarkProcessingProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessBookmarkJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarkProcessingProvider: BookmarkProcessingProvider

    override fun executeInternal(context: JobExecutionContext) {
        postProcessBookmark(context.mergedJobDataMap)
    }

    private fun postProcessBookmark(jobDataMap: JobDataMap) {
        val bookmarkId = jobDataMap.getString("id")
        logger.info("Do bookmark processing for bookmarkId: $bookmarkId")
        bookmarkProcessingProvider.processBookmark(bookmarkId)
    }
}
