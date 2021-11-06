package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.comment.getMediaDetails
import com.server.ud.enums.PostType

data class PostCommentDetailForUser(
    val userId: String,
    val commented: Boolean
)

data class CommentReportDetail(
    val postId: String,
    val comments: Long,
    val userLevelInfo: PostCommentDetailForUser
)

data class CommentReplyDetailForUser(
    val userId: String,
    val replied: Boolean
)

data class ReplyReportDetail(
    val commentId: String,
    val replies: Long,
    val userLevelInfo: CommentReplyDetailForUser
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCommentRequest(
    var postId: String,
    var postType: PostType,
    var text: String,
    var mediaDetails: MediaDetailsV2? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCommentResponse(
    var commentId: String,
    var postId: String,
    var userId: String,
    var text: String,
    var createdAt: Long,
    var mediaDetails: MediaDetailsV2?,
)

fun Comment.toSavedCommentResponse(): SavedCommentResponse {
    this.apply {
        return SavedCommentResponse(
            commentId = commentId,
            postId = postId,
            userId = userId,
            createdAt = createdAt.toEpochMilli(),
            text = text,
            mediaDetails = getMediaDetails()
        )
    }
}
