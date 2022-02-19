package com.server.ud.jobs

import com.server.ud.provider.integration.IntegrationProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class InstagramPostIngestionJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var integrationProvider: IntegrationProvider

    override fun executeInternal(context: JobExecutionContext) {
        ingestAllInstagramPosts(context.mergedJobDataMap)
    }

    private fun ingestAllInstagramPosts(jobDataMap: JobDataMap) {
        val key = jobDataMap.getString("id")
        logger.info("ingestAllInstagramPosts key: $key")
        integrationProvider.ingestAllInstagramPosts(key)
    }
}
