package com.server.ud.provider.faker

import com.github.javafaker.Faker
import com.server.dk.model.sampleVideoMedia
import com.server.ud.dto.*
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.Post
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.*
import com.server.ud.model.HashTagData
import com.server.ud.model.HashTagsList
import com.server.ud.provider.bookmark.BookmarkProvider
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.reply.ReplyProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class FakerProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var commentProvider: CommentProvider

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    @Autowired
    private lateinit var likeProvider: LikeProvider

    @Autowired
    private lateinit var bookmarkProvider: BookmarkProvider

    fun createFakeData(user: UserV2, request: FakerRequest): List<Any> {
        if (request.countOfPost > 25 ||
            request.maxCountOfComments > 25 ||
            request.maxCountOfReplies > 25) {
            error("Max of 25 fake data points in any category is allowed are allowed to be created at one time")
        }

        if (request.countOfPost < 1 ||
            request.maxCountOfComments < 1 ||
            request.maxCountOfReplies < 1) {
            error("Minimum value required is 1 for all the above fields.")
        }

        val posts = mutableListOf<Post?>()
        val comments = mutableListOf<Comment?>()
        val replies = mutableListOf<Reply?>()
        val likes = mutableListOf<Like?>()
        val bookmarks = mutableListOf<Bookmark?>()

        val faker = Faker()

        for (i in 1..request.countOfPost) {
            val req = SavePostRequest(
                postType = PostType.GENERIC_POST,
                title = faker.book().title(),
                description = faker.book().publisher(),
                tags = HashTagsList(listOf(
                    HashTagData(
                        tagId = "newhouse",
                        displayName = "newhouse",
                    ),
                    HashTagData(
                        tagId = "lakesideview",
                        displayName = "lakesideview",
                    )
                )),
                categories = setOf(CategoryV2.KITCHEN, CategoryV2.EXTERIOR),
                locationRequest = sampleLocationRequests[Random.nextInt(sampleLocationRequests.size)],
                mediaDetails = sampleVideoMedia[Random.nextInt(sampleVideoMedia.size)]
            )
            posts.add(postProvider.save(user, req))
        }


        posts.filterNotNull().map {
            val randomCount = Random.nextInt(0, request.maxCountOfComments)
            for (i in 1..randomCount) {
                comments.add(commentProvider.save(user, SaveCommentRequest(
                    postId = it.postId,
                    postType = it.postType,
                    text = faker.lorem().sentence(),
                )))
            }
        }

        comments.filterNotNull().map {
            val randomCount = Random.nextInt(0, request.maxCountOfReplies)
            for (i in 1..randomCount) {
                replies.add(replyProvider.save(user, SaveCommentReplyRequest(
                    commentId = it.commentId,
                    postId = it.postId,
                    text = faker.lorem().sentence(),
                )))
            }
        }


        posts.filterNotNull().map {
            likes.add(likeProvider.save(user, SaveLikeRequest(
                resourceType = ResourceType.POST,
                resourceId = it.postId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(user, SaveBookmarkRequest(
                    resourceType = ResourceType.POST,
                    resourceId = it.postId,
                    action = BookmarkUpdateAction.ADD,
            )))
        }

        comments.filterNotNull().map {
            likes.add(likeProvider.save(user, SaveLikeRequest(
                resourceType = ResourceType.POST_COMMENT,
                resourceId = it.commentId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(user, SaveBookmarkRequest(
                resourceType = ResourceType.POST_COMMENT,
                resourceId = it.commentId,
                action = BookmarkUpdateAction.ADD,
            )))
        }

        replies.filterNotNull().map {
            likes.add(likeProvider.save(user, SaveLikeRequest(
                resourceType = ResourceType.POST_COMMENT_REPLY,
                resourceId = it.replyId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(user, SaveBookmarkRequest(
                resourceType = ResourceType.POST_COMMENT_REPLY,
                resourceId = it.replyId,
                action = BookmarkUpdateAction.ADD,
            )))
        }


        val result = mutableListOf<Any?>()

        posts.map { result.add(it) }
        comments.map { result.add(it) }
        replies.map { result.add(it) }
        likes.map { result.add(it) }
        bookmarks.map { result.add(it) }

        return result.filterNotNull()

    }

}
