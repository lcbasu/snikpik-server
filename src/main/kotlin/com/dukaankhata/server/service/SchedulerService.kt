package com.dukaankhata.server.service

import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment

abstract class SchedulerService {
    abstract fun schedulePaymentSms(payment: Payment): Payment?
    abstract fun scheduleSalaryUpdate(employee: Employee): Employee?
}
