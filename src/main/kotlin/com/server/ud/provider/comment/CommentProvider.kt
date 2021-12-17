package com.server.ud.provider.comment

import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.provider.RandomIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.comment.CommentRepository
import com.server.ud.dto.SaveCommentRequest
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.comment.Comment
import com.server.ud.provider.deferred.DeferredProcessingProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommentProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var deferredProcessingProvider: DeferredProcessingProvider

    @Autowired
    private lateinit var commentProcessingProvider: CommentProcessingProvider

    @Autowired
    private lateinit var randomIdProvider: RandomIdProvider

    fun getComment(commentId: String): Comment? =
        try {
            val comments = commentRepository.findAllByCommentId(commentId)
            if (comments.size > 1) {
                error("More than one comment has same commentId: $commentId")
            }
            comments.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting PostComment for $commentId failed.")
            e.printStackTrace()
            null
        }

    fun save(userId: String, request: SaveCommentRequest) : Comment? {
        try {
            val comment = Comment(
                commentId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.CMT),
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                postType = request.postType,
                postId = request.postId,
                text = request.text,
                media = request.mediaDetails?.convertToString(),
            )
            val savedComment = commentRepository.save(comment)
            commentProcessingProvider.processCommentNow(savedComment)
            deferredProcessingProvider.deferProcessingForComment(savedComment.commentId)
            return savedComment
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun handleProcessedMedia(updatedMediaDetail: MediaProcessingDetail) {
        TODO("Not yet implemented")
    }

}
