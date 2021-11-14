package com.server.ud.service.scheduler

import com.server.dk.enums.JobGroupType
import org.quartz.Job

data class JobRequest(val genericId: String,
                       val job: Class<out Job?>,
                       val description: String,
                       val groupTypeForJob: JobGroupType,
                       val groupTypeForTrigger: JobGroupType,
                       val scheduleAfterSeconds: Long)

abstract class GenericSchedulerService {
    abstract fun scheduleJob(jobRequest: JobRequest)
}
