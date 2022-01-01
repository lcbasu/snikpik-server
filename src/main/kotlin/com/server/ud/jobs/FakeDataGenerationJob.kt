package com.server.ud.jobs

import com.server.ud.provider.faker.FakerProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class FakeDataGenerationJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var fakerProvider: FakerProvider

    override fun executeInternal(context: JobExecutionContext) {
        createFakeDataRandomly(context.mergedJobDataMap)
    }

    private fun createFakeDataRandomly(jobDataMap: JobDataMap) {
        val someId = jobDataMap.getString("id")
        logger.info("Generate fake data for someId: $someId")
        fakerProvider.createFakeDataRandomly()
    }
}
