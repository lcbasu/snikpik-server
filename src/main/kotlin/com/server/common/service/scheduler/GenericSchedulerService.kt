package com.server.common.service.scheduler

import com.server.common.enums.JobGroupKeyType
import com.server.common.enums.JobGroupTriggerType
import org.quartz.Job

data class JobRequest(val genericId: String,
                      val groupTypeForJob: JobGroupKeyType,
                      val groupTypeForTrigger: JobGroupTriggerType,
                      val job: Class<out Job?>? = null,
                      val description: String? = null,
                      val scheduleAfterSeconds: Long? = null,
                      val repeatAfterSeconds: Long? = null,)

abstract class GenericSchedulerService {
    abstract fun scheduleJob(jobRequest: JobRequest)
    abstract fun deleteExistingJob(jobRequest: JobRequest)
}
