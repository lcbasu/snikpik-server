package com.server.ud.service.reply

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.provider.reply.RepliesByCommentProvider
import com.server.ud.provider.reply.RepliesCountByCommentProvider
import com.server.ud.provider.reply.ReplyForCommentByUserProvider
import com.server.ud.provider.reply.ReplyProvider
import com.server.ud.provider.user.UserV2Provider
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

    @Autowired
    private lateinit var repliesByCommentProvider: RepliesByCommentProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

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

    override fun getCommentReplies(request: GetCommentRepliesRequest): CommentRepliesResponse {
        val result = repliesByCommentProvider.getCommentReplies(request)
        return CommentRepliesResponse(
            replies = result.content?.filterNotNull()?.map { it.toSavedCommentReplyResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState,
        )
    }

    override fun getSingleReplyUserDetail(userId: String): SingleReplyUserDetail {
        val user = userV2Provider.getUser(userId) ?: error("No userV2 found with id: $userId")
        return user.toSingleReplyUserDetail()
    }
}
