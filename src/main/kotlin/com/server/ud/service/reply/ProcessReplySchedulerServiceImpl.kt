package com.server.ud.service.reply

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.entities.reply.Reply
import com.server.ud.jobs.ProcessReplyJob
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessReplySchedulerServiceImpl : ProcessReplySchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun createReplyProcessingJob(reply: Reply): Reply {
        try {
            val jobDetail = getJobDetail(reply)
            val oldTrigger = getExistingTrigger(reply)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingJob(reply)
                // Make recursive call
                return createReplyProcessingJob(reply)
            } else {
                val newTrigger = createNewTrigger(reply, jobDetail)
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
            logger.info("Reply processing job scheduled for replyId: ${reply.replyId} with jobKey: ${jobDetail.key}")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for processing after commentReply creation for replyId: ${reply.replyId}")
            e.printStackTrace()
        }
        return reply
    }

    private fun deleteExistingJob(reply: Reply): Reply {
        try {
            val jobKey = getJobKey(reply)
            val triggerKey = getTriggerKey(reply)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
            logger.info("Un-scheduling Reply processing job for replyId: ${reply.replyId} with jobKey: $jobKey")
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for replyId: ${reply.replyId}")
            e.printStackTrace()
        }
        return reply
    }

    private fun getJobDetail(reply: Reply): JobDetail {
        val jobKey = getJobKey(reply)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = reply.replyId
        return JobBuilder
            .newJob(ProcessReplyJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Process Reply Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getJobKey(reply: Reply): JobKey {
        val id: String = reply.replyId
        return JobKey(id, JobGroupType.ProcessReplyJob_Job.name)
    }

    private fun getExistingTrigger(reply: Reply): Trigger? {
        val triggerKey = getTriggerKey(reply)
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

    private fun createNewTrigger(reply: Reply, jobDetail: JobDetail): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTriggerKey(reply))
            .withDescription("Do Reply processing")
            .startAt(getStartDateForAfterReplyCreated())
            .build()
    }

    private fun getTriggerKey(reply: Reply): TriggerKey {
        val id: String = reply.replyId
        return TriggerKey(id, JobGroupType.ProcessReplyJob_Trigger.name)
    }

    // Process the replies immediately
    // Creating a scheduler job so that the processing happens in
    // async manner
    private fun getStartDateForAfterReplyCreated(): Date {
        val startTime = DateUtils.dateTimeNow().plusSeconds(1)
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
