package com.server.sp.jobs

import com.server.sp.provider.user.SpUserProcessingProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessSpUserJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var spUserProcessingProvider: SpUserProcessingProvider

    override fun executeInternal(context: JobExecutionContext) {
        processUser(context.mergedJobDataMap)
    }

    private fun processUser(jobDataMap: JobDataMap) {
        val userId = jobDataMap.getString("id")
        logger.info("Do SpUser processing for userId: $userId")
        spUserProcessingProvider.processSpUser(userId)
    }
}
