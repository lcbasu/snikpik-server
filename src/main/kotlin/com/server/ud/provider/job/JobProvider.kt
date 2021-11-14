package com.server.ud.provider.job

import com.server.dk.enums.JobGroupType
import com.server.ud.jobs.*
import com.server.ud.service.scheduler.GenericSchedulerService
import com.server.ud.service.scheduler.JobRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JobProvider {

    @Autowired
    private lateinit var genericSchedulerService: GenericSchedulerService

    fun scheduleProcessingForPost(postId: String) {
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = postId,
            job = ProcessPostJob::class.java,
            description = "Process Post",
            groupTypeForJob = JobGroupType.ProcessPostJob_Job,
            groupTypeForTrigger = JobGroupType.ProcessPostJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleProcessingForReply(replyId: String) {
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = replyId,
            job = ProcessReplyJob::class.java,
            description = "Process Reply",
            groupTypeForJob = JobGroupType.ProcessReplyJob_Job,
            groupTypeForTrigger = JobGroupType.ProcessReplyJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleProcessingForBookmark(bookmarkId: String) {
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = bookmarkId,
            job = ProcessBookmarkJob::class.java,
            description = "Process Bookmark",
            groupTypeForJob = JobGroupType.ProcessBookmarkJob_Job,
            groupTypeForTrigger = JobGroupType.ProcessBookmarkJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleProcessingForComment(commentId: String) {
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = commentId,
            job = ProcessCommentJob::class.java,
            description = "Process Comment",
            groupTypeForJob = JobGroupType.ProcessCommentJob_Job,
            groupTypeForTrigger = JobGroupType.ProcessCommentJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleProcessingForLike(likeId: String) {
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = likeId,
            job = ProcessLikeJob::class.java,
            description = "Process Like",
            groupTypeForJob = JobGroupType.ProcessLikeJob_Job,
            groupTypeForTrigger = JobGroupType.ProcessLikeJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleProcessingForLocation(locationId: String) {
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = locationId,
            job = ProcessLocationJob::class.java,
            description = "Process Location",
            groupTypeForJob = JobGroupType.ProcessLocationJob_Job,
            groupTypeForTrigger = JobGroupType.ProcessLocationJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleProcessingForSocialRelation(socialRelationId: String) {
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = socialRelationId,
            job = ProcessSocialRelationJob::class.java,
            description = "Process Social Relation",
            groupTypeForJob = JobGroupType.ProcessSocialRelationJob_Job,
            groupTypeForTrigger = JobGroupType.ProcessSocialRelationJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleProcessingForUserV2(userV2Id: String) {
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = userV2Id,
            job = ProcessUserV2Job::class.java,
            description = "Process UserV2",
            groupTypeForJob = JobGroupType.ProcessUserV2Job_Job,
            groupTypeForTrigger = JobGroupType.ProcessUserV2Job_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

}
