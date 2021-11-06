package com.server.ud.service.reply

import com.server.ud.dto.ReplyReportDetail
import com.server.ud.dto.SaveCommentReplyRequest
import com.server.ud.dto.SavedCommentReplyResponse

abstract class ReplyService {
    abstract fun saveReply(request: SaveCommentReplyRequest): SavedCommentReplyResponse
    abstract fun getReplyReportDetail(commentId: String): ReplyReportDetail
}
