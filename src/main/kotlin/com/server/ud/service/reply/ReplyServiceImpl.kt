package com.server.ud.service.reply

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.provider.reply.RepliesCountByCommentProvider
import com.server.ud.provider.reply.ReplyForCommentByUserProvider
import com.server.ud.provider.reply.ReplyProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReplyServiceImpl : ReplyService() {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    @Autowired
    private lateinit var repliesCountByCommentProvider: RepliesCountByCommentProvider

    @Autowired
    private lateinit var replyForCommentByUserProvider: ReplyForCommentByUserProvider

    override fun saveReply(request: SaveCommentReplyRequest): SavedCommentReplyResponse {
        val userDetailsFromToken = securityProvider.validateRequest()
        val comment = replyProvider.save(userDetailsFromToken.getUserIdToUse(), request) ?: error("Failed to save reply for commentId: ${request.commentId}")
        return comment.toSavedCommentReplyResponse()
    }

    override fun getReplyReportDetail(commentId: String): ReplyReportDetail {
        val userDetailsFromToken = securityProvider.validateRequest()
        val repliesCount = repliesCountByCommentProvider.getRepliesCountByComment(commentId)?.repliesCount ?: 0
        val replied = replyForCommentByUserProvider.getRepliesForCommentByUser(
            commentId = commentId,
            userId = userDetailsFromToken.getUserIdToUse()
        )?.replied ?: false
        return ReplyReportDetail(
            commentId = commentId,
            replies = repliesCount,
            userLevelInfo = ReplyDetailForUser(
                userId = userDetailsFromToken.getUserIdToUse(),
                replied = replied
            )
        )
    }

}
