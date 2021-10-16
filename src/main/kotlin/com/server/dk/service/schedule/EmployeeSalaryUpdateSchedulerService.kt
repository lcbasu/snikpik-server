package com.server.dk.service.schedule

import com.server.dk.entities.Employee

abstract class EmployeeSalaryUpdateSchedulerService {
    abstract fun scheduleEmployeeSalaryUpdate(employee: Employee): Employee
    abstract fun unScheduleEmployeeSalaryUpdate(employee: Employee): Employee
}
