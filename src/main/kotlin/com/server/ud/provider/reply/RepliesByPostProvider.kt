package com.server.ud.provider.reply

import com.server.ud.dao.reply.RepliesByPostRepository
import com.server.ud.entities.reply.RepliesByPost
import com.server.ud.entities.reply.Reply
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RepliesByPostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var repliesByPostRepository: RepliesByPostRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(reply: Reply) : RepliesByPost? {
        return try {
            val repliesByPost = RepliesByPost(
                postId = reply.postId,
                commentId = reply.commentId,
                createdAt = reply.createdAt,
                replyId = reply.replyId,
                userId = reply.userId,
                replyText = reply.text,
                media = reply.media,
            )
            repliesByPostRepository.save(repliesByPost)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getReplies(postId: String): List<RepliesByPost> {
        return repliesByPostRepository.findAllByPostId(postId)
    }

    fun deletePostExpandedData(postId: String) {
        TODO("Add steps to delete post and related information")
    }
}
