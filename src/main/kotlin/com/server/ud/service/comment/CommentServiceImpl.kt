package com.server.ud.service.comment

import com.server.common.provider.AuthProvider
import com.server.ud.dto.*
import com.server.ud.provider.comment.CommentForPostByUserProvider
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.comment.CommentsByPostProvider
import com.server.ud.provider.comment.CommentsCountByPostProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl : CommentService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var commentProvider: CommentProvider

    @Autowired
    private lateinit var commentsCountByPostProvider: CommentsCountByPostProvider

    @Autowired
    private lateinit var commentForPostByUserProvider: CommentForPostByUserProvider

    @Autowired
    private lateinit var commentsByPostProvider: CommentsByPostProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    override fun saveComment(request: SaveCommentRequest): SavedCommentResponse {
        val requestContext = authProvider.validateRequest()
        val comment = commentProvider.save(requestContext.userV2, request) ?: error("Failed to save comment for postId: ${request.postId}")
        return comment.toSavedCommentResponse()
    }

    override fun getCommentReportDetail(postId: String): CommentReportDetail {
        val requestContext = authProvider.validateRequest()
        val commentsCountByResource = commentsCountByPostProvider.getCommentsCountByPost(postId)?.commentsCount ?: 0
        val commented = commentForPostByUserProvider.getCommentForPostByUser(
            postId = postId,
            userId = requestContext.userV2.userId
        )?.commented ?: false
        return CommentReportDetail(
            postId = postId,
            comments = commentsCountByResource,
            userLevelInfo = CommentDetailForUser(
                userId = requestContext.userV2.userId,
                commented = commented
            )
        )
    }

    override fun getPostComments(request: GetPostCommentsRequest): PostCommentsResponse {
        val result = commentsByPostProvider.getPostComments(request)
        return PostCommentsResponse(
            comments = result.content?.filterNotNull()?.map { it.toSingleCommentDetail() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getSingleCommentUserDetail(userId: String): SingleCommentUserDetail {
        val user = userV2Provider.getUser(userId) ?: error("No userV2 found with id: $userId")
        return user.toSingleCommentUserDetail()
    }

}
