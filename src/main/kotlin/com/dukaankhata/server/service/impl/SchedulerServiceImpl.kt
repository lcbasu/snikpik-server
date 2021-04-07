package com.dukaankhata.server.service.impl

import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.enums.SalaryType
import com.dukaankhata.server.jobs.EmployeeSalaryUpdateJob
import com.dukaankhata.server.service.SchedulerService
import com.dukaankhata.server.utils.DateUtils
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.*

@Service
class SchedulerServiceImpl : SchedulerService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun scheduleEmployeeSalaryUpdate(employee: Employee): Employee {
        try {
            val jobDetail = getEmployeeSalaryUpdateJobDetail(employee)
            val trigger = getEmployeeSalaryUpdateTrigger(employee, jobDetail)
            if (scheduler.checkExists(jobDetail.key) && !scheduler.checkExists(trigger.key)) {
                scheduler.rescheduleJob(trigger.key, trigger)
            } else if (!scheduler.checkExists(jobDetail.key) && !scheduler.checkExists(trigger.key)) {
                scheduler.scheduleJob(jobDetail, trigger)
            }
        } catch (e: Exception) {
            logger.error("[CRITICAL]Scheduling of Job failed for employee salary update for employeeId: ${employee.id}")
            e.printStackTrace()
        }
        return employee
    }

    override fun unScheduleEmployeeSalaryUpdate(employee: Employee): Employee {
        try {
            val jobDetail = getEmployeeSalaryUpdateJobDetail(employee)
            val trigger = getEmployeeSalaryUpdateTrigger(employee, jobDetail)
            scheduler.unscheduleJob(trigger.key)
        } catch (e: Exception) {
            logger.error("[CRITICAL]Un-Scheduling of Job failed for employee salary update for employeeId: ${employee.id}")
            e.printStackTrace()
        }
        return employee
    }

    private fun getEmployeeSalaryUpdateJobDetail(employee: Employee): JobDetail {
        val id: String = employee.id.toString()
        val jobKey = getEmployeeSalaryUpdateJobKey(employee)
        try {
            val jobDetail = scheduler.getJobDetail(jobKey)
            if (Objects.nonNull(jobDetail)) {
                return jobDetail
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        val jobDataMap = JobDataMap()
        jobDataMap["id"] = id
        return JobBuilder
            .newJob(EmployeeSalaryUpdateJob::class.java)
            .withIdentity(jobKey)
            .withDescription("Employee Salary Update Job")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }

    private fun getEmployeeSalaryUpdateJobKey(employee: Employee): JobKey {
        val id: String = employee.id.toString()
        return JobKey(id, "EmployeeSalaryUpdate-Job")
    }

    private fun getEmployeeSalaryUpdateTrigger(employee: Employee, jobDetail: JobDetail): Trigger {
        val triggerKey = getEmployeeSalaryUpdateTriggerKey(employee)
        try {
            val trigger = scheduler.getTrigger(triggerKey)
            if (Objects.nonNull(trigger)) {
                return trigger
            }
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail.key)
            .withIdentity(triggerKey)
            .withDescription("Workspace Subscription Renewal Trigger")
            .startAt(getStartDateForEmployeeSalaryUpdate(employee))
            .withSchedule(getScheduleForEmployeeSalaryUpdate(employee))
            .build()
    }

    private fun getEmployeeSalaryUpdateTriggerKey(employee: Employee): TriggerKey {
        val id: String = employee.id.toString()
        return TriggerKey(id, "EmployeeSalaryUpdate-Trigger")
    }

    private fun getStartDateForEmployeeSalaryUpdate(employee: Employee): Date {
        when (employee.salaryType) {
            // Paid Monthly
            SalaryType.MONTHLY, SalaryType.DAILY, SalaryType.PER_HOUR -> {
                logger.info("Get Start Time to Run Schedule to Update salary on the monthly basis")
                // This is used to set the schedule
                val salaryCycleMonthDay = employee.salaryCycle.split("_")[1].toInt()
                val now = DateUtils.dateTimeNow()
                var startDateTime = LocalDateTime.of(now.year, now.month, salaryCycleMonthDay, 0, 0, 0, 0)
                if (now.isAfter(startDateTime)) {
                    startDateTime = startDateTime.plusMonths(1)
                }
                return Date(DateUtils.getEpoch(startDateTime) * 1000)
            }
            // Paid weekly
            SalaryType.WEEKLY -> {
                logger.info("Get Start Time to Run Schedule to Update salary on the weekly basis")
                // Start on the next scheduled week day
                val now = DateUtils.dateTimeNow()
                val nextScheduleStartWeekDay = now.with(TemporalAdjusters.next(DayOfWeek.valueOf(employee.salaryCycle.split("_")[1])))
                return Date(DateUtils.getEpoch(nextScheduleStartWeekDay) * 1000)
            }
            // No Schedule
            SalaryType.ONE_TIME -> {
                logger.error("===INVALID CASE===")
                error("There should be NO schedule for this case. So in case you find one, un-schedule that job")
            }
        }
    }

    private fun getScheduleForEmployeeSalaryUpdate(employee: Employee): CalendarIntervalScheduleBuilder? {
        when (employee.salaryType) {
            // Paid Monthly
            SalaryType.MONTHLY, SalaryType.DAILY, SalaryType.PER_HOUR -> {
                logger.info("Get Schedule to Update salary on the monthly basis")
                return CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInMonths(1)
            }

            // Paid weekly
            SalaryType.WEEKLY -> {
                logger.info("Get Schedule to Update salary on the weekly basis")
                return CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInWeeks(1)
            }

            // No Schedule
            SalaryType.ONE_TIME -> {
                logger.error("===INVALID CASE===")
                error("There should be NO schedule for this case. So in case you find one, un-schedule that job")
            }
        }
    }

}
