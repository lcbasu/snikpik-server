package com.server.ud.service.post

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.entities.post.Post
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

    override fun createPostProcessingJob(post: Post): Post {
        try {
            val jobDetail = getJobDetail(post)
            val oldTrigger = getExistingTrigger(post)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(post)
                // Make recursive call
                return createPostProcessingJob(post)
            } else {
                val newTrigger = createNewTrigger(post, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("Post processing job scheduled for post: ${post.postId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after post creation for postId: ${post.postId}")
            e.printStackTrace()
        }
        return post
    }

    private fun deleteExistingJob(post: Post): Post {
        try {
            val jobKey = getJobKey(post)
            val triggerKey = getTriggerKey(post)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling Post processing job for post: ${post.postId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for companyId: ${post.postId}")
            e.printStackTrace()
        }
        return post
    }

    private fun getJobDetail(post: Post): JobDetail {
        val jobKey = getJobKey(post)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = post.postId.toString()
        return JobBuilder
            .newJob(ProcessPostJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Take Shop Online Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(post: Post): JobKey {
        val id: String = post.postId.toString()
        return JobKey(id, JobGroupType.ProcessPostJob_Job.name)
    }

    private fun getExistingTrigger(post: Post): Trigger? {
        val triggerKey = getTriggerKey(post)
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

    private fun createNewTrigger(post: Post, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(post))
            .withDescription("Do post processing for post")
            .startAt(getStartDateForAfterPostCreated())
            .build()
    }

    private fun getTriggerKey(post: Post): TriggerKey {
        val id: String = post.postId.toString()
        return TriggerKey(id, JobGroupType.ProcessPostJob_Trigger.name)
    }

    private fun getStartDateForAfterPostCreated(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(10)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
