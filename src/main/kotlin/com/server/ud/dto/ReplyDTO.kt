package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.reply.getMediaDetails
import com.server.ud.enums.PostType

@JsonIgnoreProperties(ignoreUnknown = true)
data class PCVSingleCommentReplyDetail (
    val commentId: String,
    val replyId: String,
    val text: String,
    val repliedAt: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PCVSingleCommentReplyUserDetail (
    val userId: String,
    val handle: String,
    val name: String,
    val dp: MediaDetailsV2,
    val verified: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PCVSingleCommentRepliesList(
    val replies: List<PCVSingleCommentReplyDetail>,
    override val numFound: Long,
    override val startIndex: Long,
    override val endIndex: Long,
): PaginationDetails

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReplyDetailForUser(
    val userId: String,
    val replied: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReplyReportDetail(
    val commentId: String,
    val replies: Long,
    val userLevelInfo: ReplyDetailForUser
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCommentReplyRequest(
    var commentId: String,
    var postId: String,
    var postType: PostType,
    var text: String,
    var mediaDetails: MediaDetailsV2? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCommentReplyResponse(
    var replyId: String,
    var commentId: String,
    var postId: String,
    var userId: String,
    var text: String,
    var createdAt: Long,
    var mediaDetails: MediaDetailsV2?,
)


fun Reply.toSavedCommentReplyResponse(): SavedCommentReplyResponse {
    this.apply {
        return SavedCommentReplyResponse(
            replyId = replyId,
            commentId = commentId,
            postId = postId,
            userId = userId,
            createdAt = createdAt.toEpochMilli(),
            text = text,
            mediaDetails = getMediaDetails()
        )
    }
}
