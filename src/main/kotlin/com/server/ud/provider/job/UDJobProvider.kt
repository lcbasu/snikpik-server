package com.server.ud.provider.job

import com.server.common.enums.JobGroupKeyType
import com.server.common.enums.JobGroupTriggerType
import com.server.common.service.scheduler.GenericSchedulerService
import com.server.common.service.scheduler.JobRequest
import com.server.ud.entities.integration.common.IntegrationAccountInfoByUserId
import com.server.ud.jobs.*
import com.server.ud.utils.UDKeyBuilder
import org.quartz.SimpleTrigger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UDJobProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var genericSchedulerService: GenericSchedulerService

    fun scheduleProcessingForPost(postId: String) {
        logger.info("scheduleProcessingForPost: $postId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = postId,
            job = ProcessPostJob::class.java,
            description = "Process Post",
            groupTypeForJob = JobGroupKeyType.ProcessPost_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessPost_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleProcessingForReply(replyId: String) {
        logger.info("scheduleProcessingForReply: $replyId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = replyId,
            job = ProcessReplyJob::class.java,
            description = "Process Reply",
            groupTypeForJob = JobGroupKeyType.ProcessReply_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessReply_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleProcessingForBookmark(bookmarkId: String) {
        logger.info("scheduleProcessingForBookmark: $bookmarkId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = bookmarkId,
            job = ProcessBookmarkJob::class.java,
            description = "Process Bookmark",
            groupTypeForJob = JobGroupKeyType.ProcessBookmark_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessBookmark_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleProcessingForComment(commentId: String) {
        logger.info("scheduleProcessingForComment: $commentId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = commentId,
            job = ProcessCommentJob::class.java,
            description = "Process Comment",
            groupTypeForJob = JobGroupKeyType.ProcessComment_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessComment_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleProcessingForLike(likeId: String) {
        logger.info("scheduleProcessingForLike: $likeId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = likeId,
            job = ProcessLikeJob::class.java,
            description = "Process Like",
            groupTypeForJob = JobGroupKeyType.ProcessLike_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessLike_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleProcessingForLocation(locationId: String) {
        logger.info("scheduleProcessingForLocation: $locationId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = locationId,
            job = ProcessLocationJob::class.java,
            description = "Process Location",
            groupTypeForJob = JobGroupKeyType.ProcessLocation_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessLocation_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleProcessingForSocialRelation(socialRelationId: String) {
        logger.info("scheduleProcessingForSocialRelation: $socialRelationId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = socialRelationId,
            job = ProcessSocialRelationJob::class.java,
            description = "Process Social Relation",
            groupTypeForJob = JobGroupKeyType.ProcessSocialRelation_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessSocialRelation_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleProcessingForUserV2(userV2Id: String) {
        logger.info("scheduleProcessingForUserV2: $userV2Id")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = userV2Id,
            job = ProcessUserV2Job::class.java,
            description = "Process UserV2",
            groupTypeForJob = JobGroupKeyType.ProcessUserV2_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessUserV2_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleReProcessingForUserV2(userV2Id: String) {
        logger.info("scheduleReProcessingForUserV2: $userV2Id")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = userV2Id,
            job = ReProcessUserV2Job::class.java,
            description = "Re-Process UserV2",
            groupTypeForJob = JobGroupKeyType.ReProcessUserV2_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ReProcessUserV2_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleProcessingForView(viewId: String) {
        logger.info("scheduleProcessingForView: $viewId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = viewId,
            job = ProcessResourceViewJob::class.java,
            description = "Resource View Processing",
            groupTypeForJob = JobGroupKeyType.ResourceView_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ResourceView_JobGroupTrigger,
            scheduleAfterSeconds = 0,
        ))
    }

    fun scheduleFakeDataGeneration(someId: String) {
        logger.info("scheduleFakeDataGeneration: $someId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = someId,
            job = FakeDataGenerationJob::class.java,
            description = "Generate Fake Data",
            groupTypeForJob = JobGroupKeyType.FakeDataGeneration_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.FakeDataGeneration_JobGroupTrigger,
            scheduleAfterSeconds = 1,
        ))
    }

    fun scheduleProcessingForPostForFollowers(postId: String) {
        logger.info("scheduleProcessingForPostForFollowers: $postId")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = postId,
            job = ProcessPostForFollowersJob::class.java,
            description = "Process Post for Followers",
            groupTypeForJob = JobGroupKeyType.ProcessPostForFollowers_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.ProcessPostForFollowers_JobGroupTrigger,
            scheduleAfterSeconds = 10, // Give it plenty of time to process other stuff before starting this job
        ))
    }

    fun scheduleRefreshingInstagramLongLivedToken(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId, refreshTokenSeconds: Long) {
        val key = UDKeyBuilder.getJobKeyForIntegrationAccountInfoByUserId(integrationAccountInfoByUserId)
        logger.info("scheduleRefreshingInstagramLongLivedToken: $key.")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = key,
            job = RefreshingInstagramLongLivedTokenJob::class.java,
            description = "Refresh Instagram Long-Lived Token",
            groupTypeForJob = JobGroupKeyType.RefreshingInstagramLongLivedToken_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.RefreshingInstagramLongLivedToken_JobGroupTrigger,
            scheduleAfterSeconds = 0,
            repeatInfo = JobRequest.RepeatInfo(
                repeatAfterSeconds = refreshTokenSeconds,
                totalRepeatCount = SimpleTrigger.REPEAT_INDEFINITELY,
            )
        ))
    }

    fun unScheduleRefreshingInstagramLongLivedToken(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId) {
        val key = UDKeyBuilder.getJobKeyForIntegrationAccountInfoByUserId(integrationAccountInfoByUserId)
        logger.info("unScheduleRefreshingInstagramLongLivedToken: $key.")
        genericSchedulerService.deleteExistingJob(JobRequest(
            genericId = key,
            groupTypeForJob = JobGroupKeyType.RefreshingInstagramLongLivedToken_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.RefreshingInstagramLongLivedToken_JobGroupTrigger,
        ))
    }

    fun scheduleInstagramPostIngestion(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId, repeatAfterSeconds: Long) {
        val key = UDKeyBuilder.getJobKeyForIntegrationAccountInfoByUserId(integrationAccountInfoByUserId)
        logger.info("scheduleInstagramPostIngestion: $key.")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = key,
            job = InstagramPostIngestionJob::class.java,
            description = "Ingest data from instagram into Unbox",
            groupTypeForJob = JobGroupKeyType.InstagramPostIngestion_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.InstagramPostIngestion_JobGroupTrigger,
            scheduleAfterSeconds = 0,
            repeatInfo = JobRequest.RepeatInfo(
                repeatAfterSeconds = repeatAfterSeconds,
                totalRepeatCount = SimpleTrigger.REPEAT_INDEFINITELY,
            )
        ))
    }

    fun unScheduleInstagramPostIngestion(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId) {
        val key = UDKeyBuilder.getJobKeyForIntegrationAccountInfoByUserId(integrationAccountInfoByUserId)
        logger.info("unScheduleInstagramPostIngestion: $key.")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = key,
            groupTypeForJob = JobGroupKeyType.InstagramPostIngestion_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.InstagramPostIngestion_JobGroupTrigger,
        ))
    }

    fun schedulePostDeletion(postId: String, repeatAfterSeconds: Long, totalRepeatCount: Int) {
        logger.info("schedulePostDeletion: $postId.")
        genericSchedulerService.scheduleJob(JobRequest(
            genericId = postId,
            job = PostDeletionJob::class.java,
            description = "Delete Post Job",
            groupTypeForJob = JobGroupKeyType.DeletePost_JobGroupKey,
            groupTypeForTrigger = JobGroupTriggerType.DeleteJob_JobGroupTrigger,
            scheduleAfterSeconds = 0,
            repeatInfo = JobRequest.RepeatInfo(
                repeatAfterSeconds = repeatAfterSeconds,
                totalRepeatCount = totalRepeatCount,
            )
        ))
    }
}
