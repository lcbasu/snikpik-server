package com.server.ud.jobs

import com.server.ud.provider.post.PostProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessPostForFollowersJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postProvider: PostProvider

    override fun executeInternal(context: JobExecutionContext) {
        processPostForFollowers(context.mergedJobDataMap)
    }

    private fun processPostForFollowers(jobDataMap: JobDataMap) {
        val postId = jobDataMap.getString("id")
        logger.info("Process post for followers for postId: $postId")
        postProvider.processPostForFollowers(postId)
    }
}
