package com.server.ud.jobs

import com.server.ud.provider.view.ProcessResourceViewsProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessResourceViewJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var processResourceViewsProvider: ProcessResourceViewsProvider

    override fun executeInternal(context: JobExecutionContext) {
        processResourceView(context.mergedJobDataMap)
    }

    private fun processResourceView(jobDataMap: JobDataMap) {
        val viewId = jobDataMap.getString("id")
        logger.info("Do processing for viewId: $viewId")
        processResourceViewsProvider.processResourceView(viewId)
    }
}
