package com.server.sp.jobs

import com.server.sp.provider.user.SpUserProcessingProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ReProcessSpUserJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var spUserProcessingProvider: SpUserProcessingProvider

    override fun executeInternal(context: JobExecutionContext) {
        reProcessUser(context.mergedJobDataMap)
    }

    private fun reProcessUser(jobDataMap: JobDataMap) {
        val userId = jobDataMap.getString("id")
        logger.info("Do SpUser re-processing for userId: $userId")
        spUserProcessingProvider.reProcessSpUser(userId)
    }
}
