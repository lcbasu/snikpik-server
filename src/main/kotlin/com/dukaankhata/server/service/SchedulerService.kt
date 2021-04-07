package com.dukaankhata.server.service

import com.dukaankhata.server.entities.Employee

abstract class SchedulerService {
    // Employee Salary Update Subscription
    abstract fun scheduleEmployeeSalaryUpdate(employee: Employee): Employee
    abstract fun unScheduleEmployeeSalaryUpdate(employee: Employee): Employee
//    abstract fun getEmployeeSalaryUpdateJobDetail(employee: Employee): JobDetail
//    abstract fun getEmployeeSalaryUpdateJobKey(employee: Employee): JobKey
//    abstract fun getEmployeeSalaryUpdateTrigger(employee: Employee, jobDetail: JobDetail): Trigger
//    abstract fun getEmployeeSalaryUpdateTriggerKey(employee: Employee): TriggerKey
}
