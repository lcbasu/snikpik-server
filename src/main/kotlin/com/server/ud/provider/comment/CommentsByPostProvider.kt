package com.server.ud.provider.comment

import com.server.ud.dao.comment.CommentsByPostRepository
import com.server.ud.dto.GetPostCommentsRequest
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.comment.CommentsByPost
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class CommentsByPostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var commentsByPostRepository: CommentsByPostRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(comment: Comment) : CommentsByPost? {
        try {
            val commentsByPost = CommentsByPost(
                postId = comment.postId,
                commentId = comment.commentId,
                userId = comment.userId,
                createdAt = comment.createdAt,
                postType = comment.postType,
                text = comment.text,
                media = comment.media,
            )
            return commentsByPostRepository.save(commentsByPost)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getPostComments(request: GetPostCommentsRequest): CassandraPageV2<CommentsByPost> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val comments = commentsByPostRepository.findAllByPostId(request.postId, pageRequest as Pageable)
        return CassandraPageV2(comments)
    }
}
