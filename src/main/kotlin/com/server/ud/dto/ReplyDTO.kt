package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.reply.RepliesByComment
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.reply.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.enums.PostType

//@JsonIgnoreProperties(ignoreUnknown = true)
//data class PCVSingleCommentReplyDetail (
//    val commentId: String,
//    val replyId: String,
//    val text: String,
//    val repliedAt: Long,
//)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SingleReplyUserDetail (
    val userId: String,
    val handle: String?,
    val name: String?,
    val dp: MediaDetailsV2?,
    val verified: Boolean?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommentRepliesResponse(
    val replies: List<SavedCommentReplyResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

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

data class GetCommentRepliesRequest (
    val commentId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)


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
            createdAt = DateUtils.getEpoch(createdAt),
            text = text,
            mediaDetails = getMediaDetails()
        )
    }
}

fun RepliesByComment.toSavedCommentReplyResponse(): SavedCommentReplyResponse {
    this.apply {
        return SavedCommentReplyResponse(
            replyId = replyId,
            commentId = commentId,
            postId = postId,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            text = replyText,
            mediaDetails = getMediaDetails()
        )
    }
}

fun UserV2.toSingleReplyUserDetail(): SingleReplyUserDetail {
    this.apply {
        return SingleReplyUserDetail(
            userId = userId,
            name = fullName,
            dp = getMediaDetailsForDP(),
            handle = handle,
            verified = verified,
        )
    }
}
