package com.dukaankhata.server.jobs

import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.utils.EmployeeUtils
import io.sentry.Sentry
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class EmployeeSalaryUpdateJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    override fun executeInternal(context: JobExecutionContext) {
        updateSalary(context.mergedJobDataMap)
    }

    private fun updateSalary(jobDataMap: JobDataMap) {
        val employeeId = jobDataMap.getLong("id")
        val employee: Employee = employeeUtils.getEmployee(employeeId) ?: error("Could not find employee for employeeId: $employeeId")
        employeeUtils.updateSalary(employee)
        logger.info("Salary updated for employeeId: ${employee.id}")
        Sentry.captureMessage("Salary updated for employeeId: ${employee.id}")
    }
}