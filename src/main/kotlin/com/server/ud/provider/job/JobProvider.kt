package com.server.ud.provider.job

import com.server.common.utils.DateUtils
import com.server.dk.enums.JobGroupType
import com.server.ud.jobs.*
import com.server.ud.service.scheduler.GenericSchedulerService
import com.server.ud.service.scheduler.JobRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JobProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var genericSchedulerService: GenericSchedulerService

    fun scheduleProcessingForPost(postId: String) {
        logger.info("scheduleProcessingForPost: Job scheduled for $postId")
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
        logger.info("scheduleProcessingForReply: Job scheduled for $replyId")
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
        logger.info("scheduleProcessingForBookmark: Job scheduled for $bookmarkId")
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
        logger.info("scheduleProcessingForComment: Job scheduled for $commentId")
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
        logger.info("scheduleProcessingForLike: Job scheduled for $likeId")
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
        logger.info("scheduleProcessingForLocation: Job scheduled for $locationId")
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
        logger.info("scheduleProcessingForSocialRelation: Job scheduled for $socialRelationId")
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
        logger.info("scheduleProcessingForUserV2: Job scheduled for $userV2Id")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = userV2Id,
            job = ProcessUserV2Job::class.java,
            description = "Process UserV2",
            groupTypeForJob = JobGroupType.ProcessUserV2Job_Job,
            groupTypeForTrigger = JobGroupType.ProcessUserV2Job_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleProcessingForPostForFollowers(postId: String) {
        logger.info("scheduleProcessingForPostForFollowers: Job scheduled for $postId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = postId,
            job = ProcessPostForFollowersJob::class.java,
            description = "Process Post For Followers",
            groupTypeForJob = JobGroupType.ProcessPostForFollowersJob_Job,
            groupTypeForTrigger = JobGroupType.ProcessPostForFollowersJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

    fun scheduleFakeDataGeneration() {
        val id = DateUtils.getEpochNow().toString()
        logger.info("scheduleFakeDataGeneration: Job scheduled for id: $id")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = id,
            job = CreateFakeDataJob::class.java,
            description = "Create Fake Data",
            groupTypeForJob = JobGroupType.FakeDataGenerationJob_Job,
            groupTypeForTrigger = JobGroupType.FakeDataGenerationJob_Trigger,
            scheduleAfterSeconds = 10,
        ))
    }

}
