package com.server.ud.controller

import com.server.ud.dto.ReplyReportDetail
import com.server.ud.dto.SaveCommentReplyRequest
import com.server.ud.dto.SavedCommentReplyResponse
import com.server.ud.service.reply.ReplyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/reply")
class ReplyController {

    @Autowired
    private lateinit var replyService: ReplyService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveReply(@RequestBody request: SaveCommentReplyRequest): SavedCommentReplyResponse {
        return replyService.saveReply(request)
    }

    @RequestMapping(value = ["/getReplyReportDetail"], method = [RequestMethod.GET])
    fun getReplyReportDetail(@RequestParam commentId: String): ReplyReportDetail {
        return replyService.getReplyReportDetail(commentId)
    }
}
