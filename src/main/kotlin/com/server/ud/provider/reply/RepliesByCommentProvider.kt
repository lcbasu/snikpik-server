package com.server.ud.provider.reply

import com.server.ud.dao.reply.RepliesByCommentRepository
import com.server.ud.dto.GetCommentRepliesRequest
import com.server.ud.entities.reply.RepliesByComment
import com.server.ud.entities.reply.Reply
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class RepliesByCommentProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var repliesByCommentRepository: RepliesByCommentRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(reply: Reply) : RepliesByComment? {
        try {
            val repliesByComment = RepliesByComment(
                commentId = reply.commentId,
                createdAt = reply.createdAt,
                replyId = reply.replyId,
                userId = reply.userId,
                replyText = reply.text,
                media = reply.media,
                postId = reply.postId,
            )
            return repliesByCommentRepository.save(repliesByComment)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun deleteAllRepliesByComment(reply: Reply) {
        try {
            return repliesByCommentRepository.deleteAllByCommentIdAndCreatedAtAndReplyIdAndUserId(
                reply.commentId,
                reply.createdAt,
                reply.replyId,
                reply.userId
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun deleteAllReplies(replies: List<RepliesByComment>) {
//        repliesByCommentRepository.deleteAll(replies)
//    }

    fun getAllRepliesByComment(commentId: String): List<RepliesByComment> {
        val limit = 10
        var pagingState = ""

        val replies = mutableListOf<RepliesByComment>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val repliesByComment = repliesByCommentRepository.findAllByCommentId(
            commentId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(repliesByComment)
        replies.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextRepliesByComment = repliesByCommentRepository.findAllByCommentId(
                commentId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextRepliesByComment)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            replies.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return replies
    }

    fun getCommentReplies(request: GetCommentRepliesRequest): CassandraPageV2<RepliesByComment> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val comments = repliesByCommentRepository.findAllByCommentId(request.commentId, pageRequest as Pageable)
        return CassandraPageV2(comments)
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
