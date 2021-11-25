package com.server.ud.service.queue

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.ReadableIdPrefix
import com.server.dk.enums.MessageDedupIdType
import com.server.dk.enums.MessageGroupIdType
import com.server.ud.provider.bookmark.BookmarkProcessingProvider
import com.server.ud.provider.comment.CommentProcessingProvider
import com.server.ud.provider.faker.FakerProvider
import com.server.ud.provider.like.LikeProcessingProvider
import com.server.ud.provider.location.LocationProcessingProvider
import com.server.ud.provider.post.PostProcessingProvider
import com.server.ud.provider.reply.ReplyProcessingProvider
import com.server.ud.provider.social.SocialRelationProcessingProvider
import com.server.ud.provider.user.UserV2ProcessingProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.stereotype.Service

// https://reflectoring.io/spring-cloud-aws-sqs/
@Service
class Consumer {

    private val logger = LoggerFactory.getLogger(Consumer::class.java)

    @Autowired
    private lateinit var postProcessingProvider: PostProcessingProvider

    @Autowired
    private lateinit var replyProcessingProvider: ReplyProcessingProvider

    @Autowired
    private lateinit var bookmarkProcessingProvider: BookmarkProcessingProvider

    @Autowired
    private lateinit var commentProcessingProvider: CommentProcessingProvider

    @Autowired
    private lateinit var likeProcessingProvider: LikeProcessingProvider

    @Autowired
    private lateinit var locationProcessingProvider: LocationProcessingProvider

    @Autowired
    private lateinit var socialRelationProcessingProvider: SocialRelationProcessingProvider

    @Autowired
    private lateinit var userV2ProcessingProvider: UserV2ProcessingProvider

    @Autowired
    private lateinit var fakerProvider: FakerProvider

    @SqsListener(value = ["ud-deffered-task-processing_standard"], deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS )
    fun receiveStringMessage(message: String?) {
        logger.info("Message received $message")

        GlobalScope.launch {
            val messageMap = try {
                jacksonObjectMapper().readValue(message, MutableMap::class.java)
            } catch (e: Exception) {
                logger.error("Message consumption failed for $message")
                e.printStackTrace()
                emptyMap()
            }
            logger.info("messageMap")
            logger.info(messageMap.toString())

            val messageBody =  try {
                messageMap.getOrDefault("messageBody", "NOT_FOUND").toString()
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }

            val messageGroupId = try {
                MessageGroupIdType.valueOf(messageMap["messageGroupId"].toString())
            } catch (e: Exception) {
                e.printStackTrace()
                MessageGroupIdType.UNKNOWN
            }

            val messageDeduplicationId = try {
                MessageDedupIdType.valueOf(messageMap["messageDeduplicationId"].toString())
            } catch (e: Exception) {
                e.printStackTrace()
                MessageDedupIdType.UNKNOWN
            }

            if (messageBody.startsWith(ReadableIdPrefix.PST.name) &&
                messageGroupId == MessageGroupIdType.ProcessPost_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessPost_DedupId) {
                logger.info("Process Post of with postId: $messageBody")
                postProcessingProvider.postProcessPost(messageBody)
            } else if (messageBody.startsWith(ReadableIdPrefix.RPL.name) &&
                messageGroupId == MessageGroupIdType.ProcessReply_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessReply_DedupId) {
                logger.info("Do reply processing for replyId: $messageBody")
                replyProcessingProvider.processReply(messageBody)
            } else if (messageBody.startsWith(ReadableIdPrefix.BMK.name) &&
                messageGroupId == MessageGroupIdType.ProcessBookmark_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessBookmark_DedupId) {
                logger.info("Do bookmark processing for bookmarkId: $messageBody")
                bookmarkProcessingProvider.processBookmark(messageBody)
            } else if (messageBody.startsWith(ReadableIdPrefix.CMT.name) &&
                messageGroupId == MessageGroupIdType.ProcessComment_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessComment_DedupId) {
                logger.info("Do comment processing for commentId: $messageBody")
                commentProcessingProvider.processComment(messageBody)
            } else if (messageBody.startsWith(ReadableIdPrefix.LIK.name) &&
                messageGroupId == MessageGroupIdType.ProcessLike_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessLike_DedupId) {
                logger.info("Do like processing for likeId: $messageBody")
                likeProcessingProvider.processLike(messageBody)
            } else if (messageBody.startsWith(ReadableIdPrefix.LOC.name) &&
                messageGroupId == MessageGroupIdType.ProcessLocation_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessLocation_DedupId) {
                logger.info("Do location processing for locationId: $messageBody")
                locationProcessingProvider.processLocation(messageBody)
            } else if (messageBody.startsWith(ReadableIdPrefix.USR.name) &&
                messageGroupId == MessageGroupIdType.ProcessSocialRelation_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessSocialRelation_DedupId) {
                logger.info("Do social relationship processing for socialRelationId: $messageBody")
                socialRelationProcessingProvider.processSocialRelation(messageBody)
            } else if (messageBody.startsWith(ReadableIdPrefix.USR.name) &&
                messageGroupId == MessageGroupIdType.ProcessUserV2_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessUserV2_DedupId) {
                logger.info("Do UserV2 processing for userId: $messageBody")
                userV2ProcessingProvider.processUserV2(messageBody)
            } else if (messageBody.startsWith(ReadableIdPrefix.PST.name) &&
                messageGroupId == MessageGroupIdType.ProcessPostForFollowers_GroupId &&
                messageDeduplicationId == MessageDedupIdType.ProcessPostForFollowers_DedupId) {
                logger.info("Do post processing for postId: $messageBody for followers of the original poster.")
                postProcessingProvider.processPostForFollowers(messageBody)
            } else if (messageBody.isNotBlank() &&
                messageGroupId == MessageGroupIdType.FakeDataGeneration_GroupId &&
                messageDeduplicationId == MessageDedupIdType.FakeDataGeneration_DedupId) {
                logger.info("Start Job for createFakeDataRandomly")
                fakerProvider.createFakeDataRandomly()
            } else {
                logger.error("Unknown message received $message")
            }
        }
        logger.info("Processing started in the background thread")
    }
}
