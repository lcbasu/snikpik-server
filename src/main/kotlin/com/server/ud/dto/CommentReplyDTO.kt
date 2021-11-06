package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.reply.getMediaDetails

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCommentReplyRequest(
    var commentId: String,
    var postId: String,
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
