package com.server.ud.service.user

import com.server.ud.entities.user.UserV2

abstract class ProcessUserV2SchedulerService {
    abstract fun createUserV2ProcessingJob(userV2: UserV2): UserV2
}
