package com.server.ud.service.post

import com.server.ud.entities.post.Post

abstract class ProcessPostSchedulerService {
    abstract fun createPostProcessingJob(post: Post): Post
}
