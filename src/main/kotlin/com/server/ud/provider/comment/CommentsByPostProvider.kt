package com.server.ud.provider.comment

import com.server.ud.dao.comment.CommentsByPostRepository
import com.server.ud.dto.GetPostCommentsRequest
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.comment.CommentsByPost
import com.server.common.pagination.CassandraPageV2
import com.server.common.utils.PaginationRequestUtil
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

    fun delete(comment: Comment) {
        try {
            commentsByPostRepository.deleteAllByPostIdAndCreatedAtAndCommentId(
                comment.postId,
                comment.createdAt,
                comment.commentId
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getAllCommentsByPost(postId: String): List<CommentsByPost> {
        val limit = 10
        var pagingState = ""

        val comments = mutableListOf<CommentsByPost>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = commentsByPostRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        comments.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = commentsByPostRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            comments.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return comments
    }

    fun getPostComments(request: GetPostCommentsRequest): CassandraPageV2<CommentsByPost> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val comments = commentsByPostRepository.findAllByPostId(request.postId, pageRequest as Pageable)
        return CassandraPageV2(comments)
    }
}
