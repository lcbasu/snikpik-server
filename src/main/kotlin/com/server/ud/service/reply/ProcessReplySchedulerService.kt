package com.server.ud.service.reply

import com.server.ud.entities.reply.Reply

abstract class ProcessReplySchedulerService {
    abstract fun createReplyProcessingJob(reply: Reply): Reply
}
