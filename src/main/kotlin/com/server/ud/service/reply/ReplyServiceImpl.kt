package com.server.ud.service.reply

import com.server.common.provider.AuthProvider
import com.server.ud.dto.*
import com.server.ud.provider.reply.ReplyProvider
import com.server.ud.provider.reply.RepliesCountByCommentProvider
import com.server.ud.provider.reply.ReplyForCommentByUserProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReplyServiceImpl : ReplyService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    @Autowired
    private lateinit var repliesCountByCommentProvider: RepliesCountByCommentProvider

    @Autowired
    private lateinit var replyForCommentByUserProvider: ReplyForCommentByUserProvider

    override fun saveReply(request: SaveCommentReplyRequest): SavedCommentReplyResponse {
        val requestContext = authProvider.validateRequest()
        val comment = replyProvider.save(requestContext.userV2, request) ?: error("Failed to save reply for commentId: ${request.commentId}")
        return comment.toSavedCommentReplyResponse()
    }

    override fun getReplyReportDetail(commentId: String): ReplyReportDetail {
        val requestContext = authProvider.validateRequest()
        val repliesCount = repliesCountByCommentProvider.getRepliesCountByComment(commentId)?.repliesCount ?: 0
        val replied = replyForCommentByUserProvider.getRepliesForCommentByUser(
            commentId = commentId,
            userId = requestContext.userV2.userId
        )?.replied ?: false
        return ReplyReportDetail(
            commentId = commentId,
            replies = repliesCount,
            userLevelInfo = CommentReplyDetailForUser(
                userId = requestContext.userV2.userId,
                replied = replied
            )
        )
    }

}
