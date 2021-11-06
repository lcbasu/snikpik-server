package com.server.ud.service.comment

import com.server.ud.entities.comment.Comment

abstract class ProcessCommentSchedulerService {
    abstract fun createCommentProcessingJob(comment: Comment): Comment
}
