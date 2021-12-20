package com.server.ud.provider.deferred

import com.server.common.utils.DateUtils
import com.server.ud.enums.MessageDedupIdType
import com.server.ud.enums.MessageGroupIdType
import com.server.ud.service.queue.Producer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeferredProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var producer: Producer

    fun deferProcessingForView(viewId: String) {
        logger.info("deferProcessingForView: $viewId")
        producer.sendToFifoQueue(
            messagePayload = viewId,
            messageGroupID = MessageGroupIdType.ResourceView_GroupId.name,
            messageDedupID = MessageDedupIdType.ResourceView_DedupId.name,
        )
    }

    fun deferProcessingForPost(postId: String) {
        logger.info("deferProcessingForPost: $postId")
        producer.sendToFifoQueue(
            messagePayload = postId,
            messageGroupID = MessageGroupIdType.ProcessPost_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessPost_DedupId.name,
        )
    }

    fun deferProcessingForReply(replyId: String) {
        logger.info("deferProcessingForReply: $replyId")
        producer.sendToFifoQueue(
            messagePayload = replyId,
            messageGroupID = MessageGroupIdType.ProcessReply_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessReply_DedupId.name,
        )
    }

    fun deferProcessingForBookmark(bookmarkId: String) {
        logger.info("deferProcessingForBookmark: $bookmarkId")
        producer.sendToFifoQueue(
            messagePayload = bookmarkId,
            messageGroupID = MessageGroupIdType.ProcessBookmark_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessBookmark_DedupId.name,
        )
    }

    fun deferProcessingForComment(commentId: String) {
        logger.info("deferProcessingForComment: $commentId")
        producer.sendToFifoQueue(
            messagePayload = commentId,
            messageGroupID = MessageGroupIdType.ProcessComment_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessComment_DedupId.name,
        )
    }

    fun deferProcessingForLike(likeId: String) {
        logger.info("deferProcessingForLike: $likeId")
        producer.sendToFifoQueue(
            messagePayload = likeId,
            messageGroupID = MessageGroupIdType.ProcessLike_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessLike_DedupId.name,
        )
    }

    fun deferProcessingForLocation(locationId: String) {
        logger.info("deferProcessingForLocation: $locationId")
        producer.sendToFifoQueue(
            messagePayload = locationId,
            messageGroupID = MessageGroupIdType.ProcessLocation_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessLocation_DedupId.name,
        )
    }

    fun deferProcessingForSocialRelation(socialRelationId: String) {
        logger.info("deferProcessingForSocialRelation: $socialRelationId")
        producer.sendToFifoQueue(
            messagePayload = socialRelationId,
            messageGroupID = MessageGroupIdType.ProcessSocialRelation_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessSocialRelation_DedupId.name,
        )
    }

    fun deferProcessingForUserV2(userV2Id: String) {
        logger.info("deferProcessingForUserV2: $userV2Id")
        producer.sendToFifoQueue(
            messagePayload = userV2Id,
            messageGroupID = MessageGroupIdType.ProcessUserV2_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessUserV2_DedupId.name,
        )
    }

    fun deferReProcessingForUserV2(userV2Id: String) {
        logger.info("deferReProcessingForUserV2: $userV2Id")
        producer.sendToFifoQueue(
            messagePayload = userV2Id,
            messageGroupID = MessageGroupIdType.ReProcessUserV2_GroupId.name,
            messageDedupID = MessageDedupIdType.ReProcessUserV2_DedupId.name,
        )
    }

    fun deferProcessingForPostForFollowers(postId: String) {
        logger.info("deferProcessingForPostForFollowers: $postId")
        producer.sendToFifoQueue(
            messagePayload = postId,
            messageGroupID = MessageGroupIdType.ProcessPostForFollowers_GroupId.name,
            messageDedupID = MessageDedupIdType.ProcessPostForFollowers_DedupId.name,
        )
    }

    fun deferFakeDataGeneration() {
        val id = DateUtils.getEpochNow().toString()
        logger.info("deferFakeDataGeneration: $id")
        producer.sendToFifoQueue(
            messagePayload = id,
            messageGroupID = MessageGroupIdType.FakeDataGeneration_GroupId.name,
            messageDedupID = MessageDedupIdType.FakeDataGeneration_DedupId.name,
        )
    }

}
