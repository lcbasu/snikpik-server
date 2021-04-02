package com.dukaankhata.server.service.impl

import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.service.SchedulerService
import org.quartz.Scheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class SchedulerServiceImpl : SchedulerService() {

    @Autowired
    private lateinit var scheduler: Scheduler

    override fun schedulePaymentSms(payment: Payment): Payment? {
        TODO("Not yet implemented")
    }

    override fun scheduleSalaryUpdate(employee: Employee): Employee? {
        TODO("Not yet implemented")
    }
}
