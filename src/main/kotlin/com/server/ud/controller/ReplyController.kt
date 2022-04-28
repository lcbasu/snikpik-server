package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.service.reply.ReplyService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/reply")
class ReplyController {

    @Autowired
    private lateinit var replyService: ReplyService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveReply(@RequestBody request: SaveCommentReplyRequest): SavedCommentReplyResponse? {
        return replyService.saveReply(request)
    }

    @RequestMapping(value = ["/delete"], method = [RequestMethod.POST])
    fun deleteReply(@RequestBody request: DeleteCommentReplyRequest): DeletedCommentReplyResponse? {
        return replyService.deleteReply(request)
    }

    @RequestMapping(value = ["/update"], method = [RequestMethod.POST])
    fun updateReply(@RequestBody request: UpdateCommentReplyRequest): SavedCommentReplyResponse? {
        return replyService.updateReply(request)
    }

    @RequestMapping(value = ["/getReplyReportDetail"], method = [RequestMethod.GET])
    fun getReplyReportDetail(@RequestParam commentId: String): ReplyReportDetail {
        return replyService.getReplyReportDetail(commentId)
    }

    @RequestMapping(value = ["/getCommentReplies"], method = [RequestMethod.GET])
    fun getCommentReplies(@RequestParam commentId: String,
                          @RequestParam limit: Int,
                          @RequestParam pagingState: String? = null): CommentRepliesResponse {
        return replyService.getCommentReplies(
            GetCommentRepliesRequest(
                commentId,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getSingleReplyUserDetail"], method = [RequestMethod.GET])
    fun getSingleReplyUserDetail(@RequestParam userId: String): SingleReplyUserDetail {
        return replyService.getSingleReplyUserDetail(userId)
    }
}
