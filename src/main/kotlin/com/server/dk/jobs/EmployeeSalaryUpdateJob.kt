package com.server.dk.jobs

import com.server.dk.entities.Employee
import com.server.dk.provider.EmployeeProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class EmployeeSalaryUpdateJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var employeeProvider: EmployeeProvider

    override fun executeInternal(context: JobExecutionContext) {
        updateSalary(context.mergedJobDataMap)
    }

    private fun updateSalary(jobDataMap: JobDataMap) {
        val employeeId = jobDataMap.getString("id")
        val employee: Employee = employeeProvider.getEmployee(employeeId) ?: error("Could not find employee for employeeId: $employeeId")
        employeeProvider.updateSalary(employee)
        logger.info("Salary updated for employeeId: ${employee.id}")
    }
}
