package com.server.common.service.scheduler

import com.server.common.enums.JobGroupKeyType
import com.server.common.enums.JobGroupTriggerType
import org.quartz.Job

data class JobRequest(val genericId: String,
                      val job: Class<out Job?>,
                      val description: String,
                      val groupTypeForJob: JobGroupKeyType,
                      val groupTypeForTrigger: JobGroupTriggerType,
                      val scheduleAfterSeconds: Long)

abstract class GenericSchedulerService {
    abstract fun scheduleJob(jobRequest: JobRequest)
}
