package com.server.ud.jobs

import com.server.ud.provider.location.LocationProcessingProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessLocationJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var locationProcessingProvider: LocationProcessingProvider

    override fun executeInternal(context: JobExecutionContext) {
        processLocation(context.mergedJobDataMap)
    }

    private fun processLocation(jobDataMap: JobDataMap) {
        val locationId = jobDataMap.getString("id")
        logger.info("Do location processing for locationId: $locationId")
        locationProcessingProvider.processLocation(locationId)
    }
}
