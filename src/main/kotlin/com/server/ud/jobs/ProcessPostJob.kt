package com.server.ud.jobs

import com.server.ud.provider.post.PostProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessPostJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postProvider: PostProvider

    override fun executeInternal(context: JobExecutionContext) {
        postProcessPost(context.mergedJobDataMap)
    }

    private fun postProcessPost(jobDataMap: JobDataMap) {
        val postId = jobDataMap.getString("id")
        logger.info("Do post processing for postId: $postId")
        postProvider.postProcessPost(postId)
    }
}
