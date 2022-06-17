package com.server.sp.provider.job

import com.server.common.enums.JobGroupKeyType
import com.server.common.enums.JobGroupTriggerType
import com.server.common.service.scheduler.GenericSchedulerService
import com.server.common.service.scheduler.JobRequest
import com.server.sp.jobs.ProcessSpUserJob
import com.server.sp.jobs.ReProcessSpUserJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SpJobProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var genericSchedulerService: GenericSchedulerService

    fun scheduleProcessingForSpUser(spUserId: String) {
        logger.info("scheduleProcessingForSpUser: $spUserId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = spUserId,
            job = ProcessSpUserJob::class.java,
            description = "Process SpUser",
            groupTypeForJob = JobGroupKeyType.ProcessSpUser_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessSpUser_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleReProcessingForSpUser(spUserId: String) {
        logger.info("scheduleReProcessingForSpUser: $spUserId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = spUserId,
            job = ReProcessSpUserJob::class.java,
            description = "Re-Process SpUser",
            groupTypeForJob = JobGroupKeyType.ReProcessSpUser_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ReProcessSpUser_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }
}
