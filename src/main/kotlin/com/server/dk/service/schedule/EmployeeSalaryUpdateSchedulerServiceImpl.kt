package com.server.dk.service.schedule

import com.server.dk.entities.Employee
import com.server.dk.enums.JobGroupType
import com.server.dk.enums.SalaryType
import com.server.dk.jobs.EmployeeSalaryUpdateJob
import com.server.dk.utils.DateUtils
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EmployeeSalaryUpdateSchedulerServiceImpl : EmployeeSalaryUpdateSchedulerService() {

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
        return JobKey(id, JobGroupType.EmployeeSalaryUpdate_Job.name)
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
            .withDescription("Employee Salary Update Trigger")
            .startAt(getStartDateForEmployeeSalaryUpdate(employee))
            .withSchedule(getScheduleForEmployeeSalaryUpdate(employee))
            .build()
    }

    private fun getEmployeeSalaryUpdateTriggerKey(employee: Employee): TriggerKey {
        val id: String = employee.id.toString()
        return TriggerKey(id, JobGroupType.EmployeeSalaryUpdate_Trigger.name)
    }

    private fun getStartDateForEmployeeSalaryUpdate(employee: Employee): Date {
        if (employee.salaryType == SalaryType.ONE_TIME) {
            logger.error("===INVALID CASE===")
            error("There should be NO schedule for this case. So in case you find one, un-schedule that job")
        }
        val now = DateUtils.dateTimeNow()
        return Date(DateUtils.getEpoch(now.plusMinutes(1)) * 1000)
    }

    private fun getScheduleForEmployeeSalaryUpdate(employee: Employee): CalendarIntervalScheduleBuilder? {
        if (employee.salaryType == SalaryType.ONE_TIME) {
            logger.error("===INVALID CASE===")
            error("There should be NO schedule for this case. So in case you find one, un-schedule that job")
        }
        // Run this job everyday
        return CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInDays(1)
    }

}
