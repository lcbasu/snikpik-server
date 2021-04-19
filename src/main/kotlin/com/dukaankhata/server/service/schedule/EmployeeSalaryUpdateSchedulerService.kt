package com.dukaankhata.server.service.schedule

import com.dukaankhata.server.entities.Employee

abstract class EmployeeSalaryUpdateSchedulerService {
    abstract fun scheduleEmployeeSalaryUpdate(employee: Employee): Employee
    abstract fun unScheduleEmployeeSalaryUpdate(employee: Employee): Employee
}
