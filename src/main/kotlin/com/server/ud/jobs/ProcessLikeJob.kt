package com.server.ud.jobs

import com.server.ud.provider.like.LikeProcessingProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessLikeJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likeProcessingProvider: LikeProcessingProvider

    override fun executeInternal(context: JobExecutionContext) {
        postProcessLike(context.mergedJobDataMap)
    }

    private fun postProcessLike(jobDataMap: JobDataMap) {
        val likeId = jobDataMap.getString("id")
        logger.info("Do like processing for likeId: $likeId")
        likeProcessingProvider.processLike(likeId)
    }
}
