package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.DateUtils
import com.server.common.model.MediaDetailsV2
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.comment.CommentsByPost
import com.server.ud.entities.comment.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.enums.PostType

// PostCommentView -> PCV

//data class PostCommentViewPostDetail (
//    val postId: String,
//    var title: String? = null,
//    var description: String? = null,
//    val media: MediaDetailsV2? = null,
//)

data class SingleCommentUserDetail (
    val userId: String,
    val handle: String?,
    val name: String?,
    val dp: MediaDetailsV2?,
    val verified: Boolean?,
)

data class GetPostCommentsRequest (
    val postId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

//data class SingleCommentDetail (
//    val postId: String,
//    val commentId: String,
//    val userId: String,
//    val text: String,
//    val commentedAt: Long,
//    val mediaDetails: MediaDetailsV2? = null,
//)

data class PostCommentsResponse(
    val comments: List<SavedCommentResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommentDetailForUser(
    val userId: String,
    val commented: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommentReportDetail(
    val postId: String,
    val comments: Long,
    val userLevelInfo: CommentDetailForUser
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

fun CommentsByPost.toSavedCommentResponse(): SavedCommentResponse {
    this.apply {
        return SavedCommentResponse(
            postId = postId,
            userId = userId,
            commentId = commentId,
            text = text,
            createdAt = DateUtils.getEpoch(createdAt),
            mediaDetails = getMediaDetails(),
        )
    }
}

fun UserV2.toSingleCommentUserDetail(): SingleCommentUserDetail {
    this.apply {
        return SingleCommentUserDetail(
            userId = userId,
            name = fullName,
            dp = getMediaDetailsForDP(),
            handle = handle,
            verified = verified,
        )
    }
}
