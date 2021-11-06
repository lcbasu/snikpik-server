package com.server.ud.service.social

import com.server.common.utils.CommonUtils
import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.entities.social.SocialRelation
import com.server.ud.jobs.ProcessSocialRelationJob
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessSocialRelationSchedulerServiceImpl : ProcessSocialRelationSchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun createSocialRelationProcessingJob(socialRelation: SocialRelation): SocialRelation {
        try {
            val jobDetail = getJobDetail(socialRelation)
            val oldTrigger = getExistingTrigger(socialRelation)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(socialRelation)
                // Make recursive call
                return createSocialRelationProcessingJob(socialRelation)
            } else {
                val newTrigger = createNewTrigger(socialRelation, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("SocialRelation processing job scheduled for socialRelation: ${getId(socialRelation)} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after socialRelation creation for socialRelationId: ${getId(socialRelation)}")
            e.printStackTrace()
        }
        return socialRelation
    }

    private fun deleteExistingJob(socialRelation: SocialRelation): SocialRelation {
        try {
            val jobKey = getJobKey(socialRelation)
            val triggerKey = getTriggerKey(socialRelation)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling SocialRelation processing job for socialRelation: ${getId(socialRelation)} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for companyId: ${getId(socialRelation)}")
            e.printStackTrace()
        }
        return socialRelation
    }

    private fun getJobDetail(socialRelation: SocialRelation): JobDetail {
        val jobKey = getJobKey(socialRelation)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = getId(socialRelation)
        return JobBuilder
            .newJob(ProcessSocialRelationJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Process SocialRelation Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(socialRelation: SocialRelation): JobKey {
        val id: String = getId(socialRelation)
        return JobKey(id, JobGroupType.ProcessSocialRelationJob_Job.name)
    }

    private fun getExistingTrigger(socialRelation: SocialRelation): Trigger? {
        val triggerKey = getTriggerKey(socialRelation)
        try {
            val trigger = scheduler.getTrigger(triggerKey)
            if (Objects.nonNull(trigger)) {
                return trigger
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createNewTrigger(socialRelation: SocialRelation, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(socialRelation))
            .withDescription("Do socialRelation processing")
            .startAt(getStartDateForAfterSocialRelationCreated())
            .build()
    }

    private fun getTriggerKey(socialRelation: SocialRelation): TriggerKey {
        val id: String = getId(socialRelation)
        return TriggerKey(id, JobGroupType.ProcessSocialRelationJob_Trigger.name)
    }

    private fun getStartDateForAfterSocialRelationCreated(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(2)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }

    private fun getId(socialRelation: SocialRelation) =
        "${socialRelation.fromUserId}${CommonUtils.STRING_SEPARATOR}${socialRelation.toUserId}"
}
