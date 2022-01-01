package com.server.ud.jobs

import com.server.ud.provider.comment.CommentProcessingProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessCommentJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentProcessingProvider: CommentProcessingProvider

    override fun executeInternal(context: JobExecutionContext) {
        postProcessComment(context.mergedJobDataMap)
    }

    private fun postProcessComment(jobDataMap: JobDataMap) {
        val commentId = jobDataMap.getString("id")
        logger.info("Do comment processing for commentId: $commentId")
        commentProcessingProvider.processComment(commentId)
    }
}
