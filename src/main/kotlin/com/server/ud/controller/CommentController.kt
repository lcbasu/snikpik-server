package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.service.comment.CommentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/comment")
class CommentController {

    @Autowired
    private lateinit var commentService: CommentService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveComment(@RequestBody request: SaveCommentRequest): SavedCommentResponse {
        return commentService.saveComment(request)
    }

    @RequestMapping(value = ["/getCommentReportDetail"], method = [RequestMethod.GET])
    fun getCommentReportDetail(@RequestParam postId: String): CommentReportDetail {
        return commentService.getCommentReportDetail(postId)
    }

    @RequestMapping(value = ["/getPostComments"], method = [RequestMethod.GET])
    fun getPostComments(@RequestParam postId: String,
                        @RequestParam limit: Int,
                        @RequestParam pagingState: String? = null): PostCommentsResponse {
        return commentService.getPostComments(
            GetPostCommentsRequest(
                postId,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getSingleCommentUserDetail"], method = [RequestMethod.GET])
    fun getSingleCommentUserDetail(@RequestParam userId: String): SingleCommentUserDetail {
        return commentService.getSingleCommentUserDetail(userId)
    }
}
