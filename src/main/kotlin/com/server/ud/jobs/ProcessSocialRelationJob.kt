package com.server.ud.jobs

import com.server.common.utils.CommonUtils
import com.server.ud.provider.social.SocialRelationProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class ProcessSocialRelationJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var socialRelationProvider: SocialRelationProvider

    override fun executeInternal(context: JobExecutionContext) {
        postProcessSocialRelation(context.mergedJobDataMap)
    }

    private fun postProcessSocialRelation(jobDataMap: JobDataMap) {
        val fromUserIdAndToUserId = jobDataMap.getString("id")
        val fromUserId = fromUserIdAndToUserId.split(CommonUtils.STRING_SEPARATOR).get(0)
        val toUserId = fromUserIdAndToUserId.split(CommonUtils.STRING_SEPARATOR).get(1)
        logger.info("Do social relationship processing for fromUserId: $fromUserId & toUserId: $toUserId")
        socialRelationProvider.processSocialRelation(fromUserId = fromUserId, toUserId = toUserId)
    }
}
