package com.server.ud.service.comment

import com.server.ud.dto.CommentReportDetail
import com.server.ud.dto.SaveCommentRequest
import com.server.ud.dto.SavedCommentResponse

abstract class CommentService {
    abstract fun saveComment(request: SaveCommentRequest): SavedCommentResponse
    abstract fun getCommentReportDetail(postId: String): CommentReportDetail
}
