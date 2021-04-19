package com.dukaankhata.server.service.schedule

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.enums.JobGroupType
import com.dukaankhata.server.enums.TakeShopOnlineAfter
import com.dukaankhata.server.jobs.TakeShopOnlineJob
import com.dukaankhata.server.utils.DateUtils
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class TakeShopOnlineSchedulerServiceImpl : TakeShopOnlineSchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun takeShopOnline(company: Company, takeShopOnlineAfter: TakeShopOnlineAfter): Company {
        try {
            if (takeShopOnlineAfter == TakeShopOnlineAfter.MANUALLY) {
                error("Manually updated thing should not be scheduled.")
            }
            val jobDetail = getTakeShopOnlineJobDetail(company)
            val oldTrigger = getOldTakeShopOnlineTrigger(company)
            val newTrigger = getNewTakeShopOnlineTrigger(company, jobDetail, takeShopOnlineAfter)
            if (scheduler.checkExists(jobDetail.key) || oldTrigger != null) {
                // Delete old data
                deleteExistingTakeShopOnlineJobData(company)
                // Make recursive call
                return takeShopOnline(company, takeShopOnlineAfter)
            } else {
                scheduler.scheduleJob(jobDetail, newTrigger)
            }
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for taking shop online for companyId: ${company.id}")
            e.printStackTrace()
        }
        return company
    }

    private fun deleteExistingTakeShopOnlineJobData(company: Company): Company {
        try {
            val jobKey = getTakeShopOnlineJobKey(company)
            val triggerKey = getTakeShopOnlineTriggerKey(company)
            scheduler.unscheduleJob(triggerKey)
            scheduler.deleteJob(jobKey)
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for taking shop online for companyId: ${company.id}")
            e.printStackTrace()
        }
        return company
    }

    private fun getTakeShopOnlineJobDetail(company: Company): JobDetail {
        val jobKey = getTakeShopOnlineJobKey(company)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = company.id
        return JobBuilder
            .newJob(TakeShopOnlineJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Take Shop Online Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getTakeShopOnlineJobKey(company: Company): JobKey {
        val id: String = company.id.toString()
        return JobKey(id, JobGroupType.TakeShopOnlineAfter_Job.name)
    }

    private fun getOldTakeShopOnlineTrigger(company: Company): Trigger? {
        val triggerKey = getTakeShopOnlineTriggerKey(company)
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

    private fun getNewTakeShopOnlineTrigger(company: Company, jobDetail: JobDetail, takeShopOnlineAfter: TakeShopOnlineAfter): Trigger {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(getTakeShopOnlineTriggerKey(company))
            .withDescription("Take Shop Online Trigger")
            .startAt(getStartDateForTakeShopOnline(takeShopOnlineAfter))
            .build()
    }

    private fun getTakeShopOnlineTriggerKey(company: Company): TriggerKey {
        val id: String = company.id.toString()
        return TriggerKey(id, JobGroupType.TakeShopOnlineAfter_Trigger.name)
    }

    private fun getStartDateForTakeShopOnline(takeShopOnlineAfter: TakeShopOnlineAfter): Date {
        val now = DateUtils.dateTimeNow()
        val startTime = when (takeShopOnlineAfter) {
            TakeShopOnlineAfter.MANUALLY -> error("Manually updated thing should not be scheduled.")
            TakeShopOnlineAfter.AFTER_ONE_HR -> now.plusHours(1)
            TakeShopOnlineAfter.AFTER_TWO_HR -> now.plusHours(2)
            TakeShopOnlineAfter.AFTER_FOUR_HR -> now.plusHours(4)
            TakeShopOnlineAfter.SAME_TIME_TOMORROW -> now.plusHours(24)
        }
        return Date(DateUtils.getEpoch(startTime) * 1000)
    }
}
