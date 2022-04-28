package com.server.ud.service.reply

import com.server.ud.dto.*

abstract class ReplyService {
    abstract fun saveReply(request: SaveCommentReplyRequest): SavedCommentReplyResponse?
    abstract fun getReplyReportDetail(commentId: String): ReplyReportDetail
    abstract fun getCommentReplies(request: GetCommentRepliesRequest): CommentRepliesResponse
    abstract fun getSingleReplyUserDetail(userId: String): SingleReplyUserDetail
    abstract fun deleteReply(request: DeleteCommentReplyRequest): DeletedCommentReplyResponse?
    abstract fun updateReply(request: UpdateCommentReplyRequest): SavedCommentReplyResponse?
}
