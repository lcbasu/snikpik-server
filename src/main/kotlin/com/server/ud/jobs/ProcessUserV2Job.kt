package com.server.ud.jobs

import com.server.ud.provider.user.UserV2ProcessingProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessUserV2Job: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2ProcessingProvider: UserV2ProcessingProvider

    override fun executeInternal(context: JobExecutionContext) {
        processUser(context.mergedJobDataMap)
    }

    private fun processUser(jobDataMap: JobDataMap) {
        val userId = jobDataMap.getString("id")
        logger.info("Do UserV2 processing for userId: $userId")
        userV2ProcessingProvider.processUserV2(userId)
    }
}
