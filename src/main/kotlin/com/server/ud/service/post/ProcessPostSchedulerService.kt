package com.server.ud.service.post

abstract class ProcessPostSchedulerService {
    abstract fun createPostProcessingJob(postId: String): String
}
