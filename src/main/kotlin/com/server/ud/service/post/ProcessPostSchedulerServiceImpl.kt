package com.server.ud.service.post

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.jobs.ProcessPostJob
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessPostSchedulerServiceImpl : ProcessPostSchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun createPostProcessingJob(postId: String): String {
        try {
            val jobDetail = getJobDetail(postId)
            val oldTrigger = getExistingTrigger(postId)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(postId)
                // Make recursive call
                return createPostProcessingJob(postId)
            } else {
                val newTrigger = createNewTrigger(postId, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("Post processing job scheduled for post: ${postId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after post creation for postId: ${postId}")
            e.printStackTrace()
        }
        return postId
    }

    private fun deleteExistingJob(postId: String): String {
        try {
            val jobKey = getJobKey(postId)
            val triggerKey = getTriggerKey(postId)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling Post processing job for post: ${postId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for companyId: ${postId}")
            e.printStackTrace()
        }
        return postId
    }

    private fun getJobDetail(postId: String): JobDetail {
        val jobKey = getJobKey(postId)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = postId
        return JobBuilder
            .newJob(ProcessPostJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Process Post Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(postId: String): JobKey {
        return JobKey(postId, JobGroupType.ProcessPostJob_Job.name)
    }

    private fun getExistingTrigger(postId: String): Trigger? {
        val triggerKey = getTriggerKey(postId)
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

    private fun createNewTrigger(postId: String, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(postId))
            .withDescription("Do post processing")
            .startAt(getStartDateForAfterPostCreated())
            .build()
    }

    private fun getTriggerKey(postId: String): TriggerKey {
        return TriggerKey(postId, JobGroupType.ProcessPostJob_Trigger.name)
    }

    private fun getStartDateForAfterPostCreated(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(5)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
