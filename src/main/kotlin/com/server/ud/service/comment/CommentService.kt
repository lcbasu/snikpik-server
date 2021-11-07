package com.server.ud.service.comment

import com.server.ud.dto.*

abstract class CommentService {
    abstract fun saveComment(request: SaveCommentRequest): SavedCommentResponse
    abstract fun getCommentReportDetail(postId: String): CommentReportDetail
    abstract fun getPostComments(request: GetPostCommentsRequest): PostCommentsResponse
    abstract fun getSingleCommentUserDetail(userId: String): SingleCommentUserDetail
}
