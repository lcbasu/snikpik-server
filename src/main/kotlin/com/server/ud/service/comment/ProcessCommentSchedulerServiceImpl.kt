package com.server.ud.service.comment

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.entities.comment.Comment
import com.server.ud.jobs.ProcessCommentJob
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessCommentSchedulerServiceImpl : ProcessCommentSchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun createCommentProcessingJob(comment: Comment): Comment {
        try {
            val jobDetail = getJobDetail(comment)
            val oldTrigger = getExistingTrigger(comment)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(comment)
                // Make recursive call
                return createCommentProcessingJob(comment)
            } else {
                val newTrigger = createNewTrigger(comment, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("PostComment processing job scheduled for commentId: ${comment.commentId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after postComment creation for commentId: ${comment.commentId}")
            e.printStackTrace()
        }
        return comment
    }

    private fun deleteExistingJob(comment: Comment): Comment {
        try {
            val jobKey = getJobKey(comment)
            val triggerKey = getTriggerKey(comment)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling PostComment processing job for commentId: ${comment.commentId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for commentId: ${comment.commentId}")
            e.printStackTrace()
        }
        return comment
    }

    private fun getJobDetail(comment: Comment): JobDetail {
        val jobKey = getJobKey(comment)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = comment.commentId
        return JobBuilder
            .newJob(ProcessCommentJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Process PostComment Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(comment: Comment): JobKey {
        val id: String = comment.commentId
        return JobKey(id, JobGroupType.ProcessCommentJob_Job.name)
    }

    private fun getExistingTrigger(comment: Comment): Trigger? {
        val triggerKey = getTriggerKey(comment)
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

    private fun createNewTrigger(comment: Comment, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(comment))
            .withDescription("Do PostComment processing")
            .startAt(getStartDateForAfterPostCommentCreated())
            .build()
    }

    private fun getTriggerKey(comment: Comment): TriggerKey {
        val id: String = comment.commentId
        return TriggerKey(id, JobGroupType.ProcessCommentJob_Trigger.name)
    }

    // Process the comments immediately
    // Creating a scheduler job so that the processing happens in
    // async manner
    private fun getStartDateForAfterPostCommentCreated(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(1)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
