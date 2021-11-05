package com.server.ud.service.like

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.entities.like.Like
import com.server.ud.jobs.ProcessLikeJob
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessLikeSchedulerServiceImpl : ProcessLikeSchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun createLikeProcessingJob(like: Like): Like {
        try {
            val jobDetail = getJobDetail(like)
            val oldTrigger = getExistingTrigger(like)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(like)
                // Make recursive call
                return createLikeProcessingJob(like)
            } else {
                val newTrigger = createNewTrigger(like, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("Like processing job scheduled for like: ${like.likeId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after like creation for likeId: ${like.likeId}")
            e.printStackTrace()
        }
        return like
    }

    private fun deleteExistingJob(like: Like): Like {
        try {
            val jobKey = getJobKey(like)
            val triggerKey = getTriggerKey(like)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling Like processing job for like: ${like.likeId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for companyId: ${like.likeId}")
            e.printStackTrace()
        }
        return like
    }

    private fun getJobDetail(like: Like): JobDetail {
        val jobKey = getJobKey(like)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = like.likeId.toString()
        return JobBuilder
            .newJob(ProcessLikeJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Process Like Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(like: Like): JobKey {
        val id: String = like.likeId.toString()
        return JobKey(id, JobGroupType.ProcessLikeJob_Job.name)
    }

    private fun getExistingTrigger(like: Like): Trigger? {
        val triggerKey = getTriggerKey(like)
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

    private fun createNewTrigger(like: Like, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(like))
            .withDescription("Do like processing")
            .startAt(getStartDateForAfterLikeCreated())
            .build()
    }

    private fun getTriggerKey(like: Like): TriggerKey {
        val id: String = like.likeId.toString()
        return TriggerKey(id, JobGroupType.ProcessLikeJob_Trigger.name)
    }

    private fun getStartDateForAfterLikeCreated(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(5)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
