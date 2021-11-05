package com.server.ud.service.like

import com.server.ud.entities.like.Like

abstract class ProcessLikeSchedulerService {
    abstract fun createLikeProcessingJob(like: Like): Like
}
