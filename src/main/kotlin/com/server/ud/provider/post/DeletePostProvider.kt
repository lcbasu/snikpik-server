package com.server.ud.provider.post

import com.server.ud.entities.post.Post
import com.server.ud.provider.bookmark.BookmarkProcessingProvider
import com.server.ud.provider.comment.CommentProcessingProvider
import com.server.ud.provider.like.LikeProcessingProvider
import com.server.ud.provider.reply.ReplyProcessingProvider
import com.server.ud.provider.search.SearchProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeletePostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarkProcessingProvider: BookmarkProcessingProvider

    @Autowired
    private lateinit var commentProcessingProvider: CommentProcessingProvider

    @Autowired
    private lateinit var likeProcessingProvider: LikeProcessingProvider

    @Autowired
    private lateinit var postProcessingProvider: PostProcessingProvider

    @Autowired
    private lateinit var replyProcessingProvider: ReplyProcessingProvider

    @Autowired
    private lateinit var searchProvider: SearchProvider

    fun deletePostExpandedData(post: Post, userId: String) {
        GlobalScope.launch {
            logger.info("Start: Delete: ${post.postId} for $userId")
            bookmarkProcessingProvider.deletePostExpandedData(post.postId)
            commentProcessingProvider.deletePostExpandedData(post.postId)
            likeProcessingProvider.deletePostExpandedData(post.postId)
            postProcessingProvider.deletePostExpandedData(post, userId)
            replyProcessingProvider.deletePostExpandedData(post.postId)
            searchProvider.deletePostExpandedData(post.postId)
            logger.info("End: Delete: ${post.postId} for $userId")
        }
    }

    // This is a synchronous call
    fun deletePostFromExplore(post: Post) {
        postProcessingProvider.deletePostFromExplore(post)
    }

}
