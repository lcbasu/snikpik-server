package com.server.ud.provider.reply

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.RandomIdProvider
import com.server.common.utils.DateUtils
import com.server.dk.model.convertToString
import com.server.ud.dao.reply.CommentReplyRepository
import com.server.ud.dto.SaveCommentReplyRequest
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.reply.Reply
import com.server.ud.provider.deferred.DeferredProcessingProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReplyProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentReplyRepository: CommentReplyRepository

    @Autowired
    private lateinit var deferredProcessingProvider: DeferredProcessingProvider

    @Autowired
    private lateinit var randomIdProvider: RandomIdProvider

    fun getCommentReply(replyId: String): Reply? =
        try {
            val replies = commentReplyRepository.findAllByReplyId(replyId)
            if (replies.size > 1) {
                error("More than one reply has same replyId: $replyId")
            }
            replies.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting CommentReply for $replyId failed.")
            e.printStackTrace()
            null
        }

    fun save(userId: String, request: SaveCommentReplyRequest) : Reply? {
        try {
            val reply = Reply(
                replyId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.RPL),
                commentId = request.commentId,
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                postId = request.postId,
                postType = request.postType,
                text = request.text,
                media = request.mediaDetails?.convertToString(),
            )
            val savedReply = commentReplyRepository.save(reply)
            deferredProcessingProvider.deferProcessingForReply(savedReply.replyId)
            return savedReply
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun handleProcessedMedia(updatedMediaDetail: MediaProcessingDetail) {
        TODO("Not yet implemented")
    }
}
