package com.server.ud.jobs

import com.server.ud.provider.reply.ReplyProcessingProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessReplyJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var replyProcessingProvider: ReplyProcessingProvider

    override fun executeInternal(context: JobExecutionContext) {
        postProcessReply(context.mergedJobDataMap)
    }

    private fun postProcessReply(jobDataMap: JobDataMap) {
        val replyId = jobDataMap.getString("id")
        logger.info("Do reply processing for replyId: $replyId")
        replyProcessingProvider.processReply(replyId)
    }
}
