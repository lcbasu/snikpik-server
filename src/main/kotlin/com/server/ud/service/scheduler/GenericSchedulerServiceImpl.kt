package com.server.ud.service.scheduler

import com.server.common.utils.DateUtils
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class GenericSchedulerServiceImpl : GenericSchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun scheduleJob(jobRequest: JobRequest) {
        try {
            val jobDetail = getJobDetail(jobRequest)
            val oldTrigger = getExistingTrigger(jobRequest)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(jobRequest)
                // Make recursive call
                return scheduleJob(jobRequest)
            } else {
                val newTrigger = createNewTrigger(jobRequest, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("Job scheduled for: ${jobRequest.groupTypeForJob.name} with genericId: ${jobRequest.genericId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for: ${jobRequest.groupTypeForJob.name} with genericId: ${jobRequest.genericId}")
            e.printStackTrace()
        }
    }

    private fun deleteExistingJob(jobRequest: JobRequest) {
        try {
            val jobKey = getJobKey(jobRequest)
            val triggerKey = getTriggerKey(jobRequest)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling job for: ${jobRequest.groupTypeForJob.name} with genericId: ${jobRequest.genericId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for: ${jobRequest.groupTypeForJob.name} with genericId: ${jobRequest.genericId}")
            e.printStackTrace()
        }
    }

    private fun getJobDetail(jobRequest: JobRequest): JobDetail {
        val jobKey = getJobKey(jobRequest)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = jobRequest.genericId
        return JobBuilder
            .newJob(jobRequest.job)
            .withIdentity(jobKey)
            .withDescription(jobRequest.description)
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(jobRequest: JobRequest): JobKey {
        return JobKey(jobRequest.genericId, jobRequest.groupTypeForJob.name)
    }

    private fun getExistingTrigger(jobRequest: JobRequest): Trigger? {
        val triggerKey = getTriggerKey(jobRequest)
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

    private fun createNewTrigger(jobRequest: JobRequest, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(jobRequest))
            .withDescription(jobRequest.description)
            .startAt(getStartDateForJob(jobRequest))
            .build()
    }

    private fun getTriggerKey(jobRequest: JobRequest): TriggerKey {
        return TriggerKey(jobRequest.genericId, jobRequest.groupTypeForTrigger.name)
    }

    private fun getStartDateForJob(jobRequest: JobRequest): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(jobRequest.scheduleAfterSeconds)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
