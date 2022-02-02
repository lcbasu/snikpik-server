package com.server.ud.provider.post

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

    fun deletePostExpandedData(postId: String, userId: String) {
        GlobalScope.launch {
            logger.info("Start: Delete: $postId for $userId")
            bookmarkProcessingProvider.deletePostExpandedData(postId)
            commentProcessingProvider.deletePostExpandedData(postId)
            likeProcessingProvider.deletePostExpandedData(postId)
            postProcessingProvider.deletePostExpandedData(postId, userId)
            replyProcessingProvider.deletePostExpandedData(postId)
            searchProvider.deletePostExpandedData(postId)
            logger.info("End: Delete: $postId for $userId")
        }
    }

    // This is a synchronous call
    fun deletePostFromExplore(postId: String) {
        postProcessingProvider.deletePostFromExplore(postId)
    }

}
