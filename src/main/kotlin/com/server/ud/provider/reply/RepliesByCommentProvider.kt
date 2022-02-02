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

    fun getCommentReplies(request: GetCommentRepliesRequest): CassandraPageV2<RepliesByComment> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val comments = repliesByCommentRepository.findAllByCommentId(request.commentId, pageRequest as Pageable)
        return CassandraPageV2(comments)
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
