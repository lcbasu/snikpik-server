package com.server.ud.service.user

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.entities.user.UserV2
import com.server.ud.jobs.ProcessUserV2Job
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessUserV2SchedulerServiceImpl : ProcessUserV2SchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun createUserV2ProcessingJob(userV2: UserV2): UserV2 {
        try {
            val jobDetail = getJobDetail(userV2)
            val oldTrigger = getExistingTrigger(userV2)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(userV2)
                // Make recursive call
                return createUserV2ProcessingJob(userV2)
            } else {
                val newTrigger = createNewTrigger(userV2, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("UserV2 processing job scheduled for userV2: ${userV2.userId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after userV2 creation for userId: ${userV2.userId}")
            e.printStackTrace()
        }
        return userV2
    }

    private fun deleteExistingJob(userV2: UserV2): UserV2 {
        try {
            val jobKey = getJobKey(userV2)
            val triggerKey = getTriggerKey(userV2)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling UserV2 processing job for userV2: ${userV2.userId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for companyId: ${userV2.userId}")
            e.printStackTrace()
        }
        return userV2
    }

    private fun getJobDetail(userV2: UserV2): JobDetail {
        val jobKey = getJobKey(userV2)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = userV2.userId
        return JobBuilder
            .newJob(ProcessUserV2Job::class.java)
            .withIdentity(jobKey)
            .withDescription("Process UserV2 Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(userV2: UserV2): JobKey {
        val id: String = userV2.userId
        return JobKey(id, JobGroupType.ProcessUserV2Job_Job.name)
    }

    private fun getExistingTrigger(userV2: UserV2): Trigger? {
        val triggerKey = getTriggerKey(userV2)
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

    private fun createNewTrigger(userV2: UserV2, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(userV2))
            .withDescription("Do userV2 processing")
            .startAt(getStartDateForAfterUserV2Created())
            .build()
    }

    private fun getTriggerKey(userV2: UserV2): TriggerKey {
        val id: String = userV2.userId
        return TriggerKey(id, JobGroupType.ProcessUserV2Job_Trigger.name)
    }

    private fun getStartDateForAfterUserV2Created(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(10)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
