package com.server.ud.service.bookmark

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.jobs.ProcessBookmarkJob
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessBookmarkSchedulerServiceImpl : ProcessBookmarkSchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun createBookmarkProcessingJob(bookmark: Bookmark): Bookmark {
        try {
            val jobDetail = getJobDetail(bookmark)
            val oldTrigger = getExistingTrigger(bookmark)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(bookmark)
                // Make recursive call
                return createBookmarkProcessingJob(bookmark)
            } else {
                val newTrigger = createNewTrigger(bookmark, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("Bookmark processing job scheduled for bookmark: ${bookmark.bookmarkId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after bookmark creation for bookmarkId: ${bookmark.bookmarkId}")
            e.printStackTrace()
        }
        return bookmark
    }

    private fun deleteExistingJob(bookmark: Bookmark): Bookmark {
        try {
            val jobKey = getJobKey(bookmark)
            val triggerKey = getTriggerKey(bookmark)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling Bookmark processing job for bookmark: ${bookmark.bookmarkId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for companyId: ${bookmark.bookmarkId}")
            e.printStackTrace()
        }
        return bookmark
    }

    private fun getJobDetail(bookmark: Bookmark): JobDetail {
        val jobKey = getJobKey(bookmark)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = bookmark.bookmarkId.toString()
        return JobBuilder
            .newJob(ProcessBookmarkJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Process Bookmark Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(bookmark: Bookmark): JobKey {
        val id: String = bookmark.bookmarkId.toString()
        return JobKey(id, JobGroupType.ProcessBookmarkJob_Job.name)
    }

    private fun getExistingTrigger(bookmark: Bookmark): Trigger? {
        val triggerKey = getTriggerKey(bookmark)
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

    private fun createNewTrigger(bookmark: Bookmark, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(bookmark))
            .withDescription("Do bookmark processing")
            .startAt(getStartDateForAfterBookmarkCreated())
            .build()
    }

    private fun getTriggerKey(bookmark: Bookmark): TriggerKey {
        val id: String = bookmark.bookmarkId.toString()
        return TriggerKey(id, JobGroupType.ProcessBookmarkJob_Trigger.name)
    }

    private fun getStartDateForAfterBookmarkCreated(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(10)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
