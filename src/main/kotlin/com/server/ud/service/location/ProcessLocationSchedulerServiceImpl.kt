package com.server.ud.service.location

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.entities.location.Location
import com.server.ud.jobs.ProcessLocationJob
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessLocationSchedulerServiceImpl : ProcessLocationSchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun createLocationProcessingJob(location: Location): Location {
        try {
            val jobDetail = getJobDetail(location)
            val oldTrigger = getExistingTrigger(location)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(location)
                // Make recursive call
                return createLocationProcessingJob(location)
            } else {
                val newTrigger = createNewTrigger(location, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("Location processing job scheduled for location: ${location.locationId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after location creation for locationId: ${location.locationId}")
            e.printStackTrace()
        }
        return location
    }

    private fun deleteExistingJob(location: Location): Location {
        try {
            val jobKey = getJobKey(location)
            val triggerKey = getTriggerKey(location)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling Location processing job for location: ${location.locationId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for companyId: ${location.locationId}")
            e.printStackTrace()
        }
        return location
    }

    private fun getJobDetail(location: Location): JobDetail {
        val jobKey = getJobKey(location)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = location.locationId
        return JobBuilder
            .newJob(ProcessLocationJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Process Location Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(location: Location): JobKey {
        val id: String = location.locationId
        return JobKey(id, JobGroupType.ProcessLocationJob_Job.name)
    }

    private fun getExistingTrigger(location: Location): Trigger? {
        val triggerKey = getTriggerKey(location)
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

    private fun createNewTrigger(location: Location, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(location))
            .withDescription("Do location processing")
            .startAt(getStartDateForAfterLocationCreated())
            .build()
    }

    private fun getTriggerKey(location: Location): TriggerKey {
        val id: String = location.locationId
        return TriggerKey(id, JobGroupType.ProcessLocationJob_Trigger.name)
    }

    private fun getStartDateForAfterLocationCreated(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(5)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
