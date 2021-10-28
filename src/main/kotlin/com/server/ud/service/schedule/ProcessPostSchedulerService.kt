package com.server.dk.service.schedule

import com.server.ud.entities.Post

abstract class ProcessPostSchedulerService {
    abstract fun createPostProcessingJob(post: Post): Post
}
