package com.server.ud.provider.user_activity

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.RandomIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.user_activity.*
import com.server.ud.dto.ByUserActivitiesFeedRequest
import com.server.ud.dto.ForUserActivitiesFeedRequest
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.Post
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user_activity.*
import com.server.ud.enums.PostType
import com.server.ud.enums.ResourceType
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.toUserAggregateActivityType
import com.server.ud.model.*
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.reply.ReplyProvider
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UserActivitiesProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userActivitiesRepository: UserActivitiesRepository

    @Autowired
    private lateinit var userActivitiesForUserRepository: UserActivitiesForUserRepository

    @Autowired
    private lateinit var userActivitiesForUserAndAggregateRepository: UserActivitiesForUserAndAggregateRepository

    @Autowired
    private lateinit var userActivitiesByUserRepository: UserActivitiesByUserRepository

    @Autowired
    private lateinit var userActivitiesByUserAndAggregateRepository: UserActivitiesByUserAndAggregateRepository

    @Autowired
    private lateinit var randomIdProvider: RandomIdProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var commentProvider: CommentProvider

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun getActivitiesFeedForUser(request: ForUserActivitiesFeedRequest): CassandraPageV2<UserActivityForUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val activities = request.userAggregateActivityType?.let {
            userActivitiesForUserAndAggregateRepository.findAllByForUserIdAndUserAggregateActivityType(request.forUserId, request.userAggregateActivityType, pageRequest as Pageable)
                .map { it.toUserActivityForUser() }
        } ?: userActivitiesForUserRepository.findAllByForUserId(request.forUserId, pageRequest as Pageable)
        return CassandraPageV2(activities)
    }

    fun getActivitiesFeedByUser(request: ByUserActivitiesFeedRequest): CassandraPageV2<UserActivityByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val activities = request.userAggregateActivityType?.let {
            userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndUserAggregateActivityType(request.byUserId, request.userAggregateActivityType, pageRequest as Pageable)
                .map { it.toUserActivityByUser() }
        } ?: userActivitiesByUserRepository.findAllByByUserId(request.byUserId, pageRequest as Pageable)
        return CassandraPageV2(activities)
    }

    private fun asyncSaveUserActivity(userActivity: UserActivity) {
        GlobalScope.launch {
            userActivitiesRepository.save(userActivity)
            userActivity.getUserActivityByUser().let {
                userActivitiesByUserRepository.save(it!!)
            }
            userActivity.getUserActivityByUserAndAggregate().let {
                userActivitiesByUserAndAggregateRepository.save(it!!)
            }
            userActivity.getUserActivityForUser().let {
                userActivitiesForUserRepository.save(it!!)
            }
            userActivity.getUserActivityForUserAndAggregate().let {
                userActivitiesForUserAndAggregateRepository.save(it!!)
            }
        }
    }

    fun savePostCreationActivity(post: Post) {
        val userActivityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_CREATED else UserActivityType.WALL_CREATED
        savePostActivity(post.userId, null, post, userActivityType)
    }

    private fun savePostActivity(byUserId: String, forUserId: String?, post: Post, userActivityType: UserActivityType) {
        try {
            val data = when (userActivityType) {
                UserActivityType.POST_CREATED,
                UserActivityType.POST_LIKED,
                UserActivityType.POST_SAVED,
                UserActivityType.POST_SHARED,
                UserActivityType.WALL_CREATED,
                UserActivityType.WALL_LIKED,
                UserActivityType.WALL_SAVED,
                UserActivityType.WALL_SHARED -> PostLevelUserActivity(
                    activityByUserId = post.userId,
                    postId = post.postId,
                    postType = post.postType,
                    postUserId = post.userId,
                    mediaDetails = post.media,
                    title = post.title,
                    description = post.description,
                    userActivityType = userActivityType,
                )
                else -> error("Incorrect User Activity Method called for postId: ${post.postId} for userActivityType: $userActivityType")
            }
            asyncSaveUserActivity(UserActivity(
                userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                createdAt = DateUtils.getInstantNow(),
                userAggregateActivityType = userActivityType.toUserAggregateActivityType(),
                userActivityType = userActivityType,
                byUserId = byUserId,
                forUserId = forUserId,
                userActivityData = data.convertToString(),
            ))
        } catch (e: Exception) {
            logger.error("Saving UserActivity for postId: ${post.postId} for userActivityType: $userActivityType failed.")
            e.printStackTrace()
        }
    }

    fun saveCommentCreationActivity(comment: Comment) {
        try {
            val post = postProvider.getPost(comment.postId) ?: error("Error while getting post with postId: ${comment.postId}")
            val userActivityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.COMMENTED_ON_POST else UserActivityType.COMMENTED_ON_WALL
            saveCommentActivity(comment.userId, post.userId, post, comment, userActivityType)
        } catch (e: Exception) {
            logger.error("Error while getting post with postId: ${comment.postId}")
            e.printStackTrace()
        }
    }

    private fun saveCommentActivity(byUserId: String, forUserId: String?, post: Post, comment: Comment, userActivityType: UserActivityType) {
        try {
            val data = when (userActivityType) {
                UserActivityType.COMMENTED_ON_POST,
                UserActivityType.POST_COMMENT_LIKED,
                UserActivityType.COMMENTED_ON_WALL,
                UserActivityType.WALL_COMMENT_LIKED -> CommentLevelUserActivity(
                    activityByUserId = comment.userId,

                    commentId = comment.commentId,
                    commentUserId = comment.userId,
                    commentText = comment.text,
                    commentMediaDetails = comment.media,

                    postId = post.postId,
                    postType = post.postType,
                    postUserId = post.userId,
                    postMediaDetails = post.media,
                    postTitle = post.title,
                    postDescription = post.description,
                    userActivityType = userActivityType,
                )
                else -> error("Incorrect User Activity Method called for postId: ${post.postId}, commentId: ${comment.commentId} for userActivityType: $userActivityType")
            }
            asyncSaveUserActivity(UserActivity(
                userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                createdAt = DateUtils.getInstantNow(),
                userAggregateActivityType = userActivityType.toUserAggregateActivityType(),
                userActivityType = userActivityType,
                byUserId = byUserId,
                forUserId = forUserId,
                userActivityData = data.convertToString(),
            ))
        } catch (e: Exception) {
            logger.error("Saving UserActivity for postId: ${post.postId}, commentId: ${comment.commentId} for userActivityType: $userActivityType failed.")
            e.printStackTrace()
        }
    }

    fun saveReplyCreationActivity(reply: Reply) {
        try {
            val comment = commentProvider.getComment(reply.commentId) ?: error("Error while getting comment with commentId: ${reply.commentId}")
            val post = postProvider.getPost(comment.postId) ?: error("Error while getting post with postId: ${comment.postId}")
            val userActivityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.REPLIED_TO_POST_COMMENT else UserActivityType.REPLIED_TO_WALL_COMMENT
            saveReplyActivity(reply.userId, comment.userId, post, comment, reply, userActivityType)
        } catch (e: Exception) {
            logger.error("Error while getting comment with commentId: ${reply.commentId}")
            e.printStackTrace()
        }
    }

    private fun saveReplyActivity(byUserId: String, forUserId: String?, post: Post, comment: Comment, reply: Reply, userActivityType: UserActivityType) {
        try {
            val data = when (userActivityType) {
                UserActivityType.REPLIED_TO_POST_COMMENT,
                UserActivityType.POST_COMMENT_REPLY_LIKED,
                UserActivityType.REPLIED_TO_WALL_COMMENT,
                UserActivityType.WALL_COMMENT_REPLY_LIKED -> ReplyLevelUserActivity(
                    activityByUserId = reply.userId,

                    replyId = reply.replyId,
                    replyUserId = reply.userId,
                    replyText = reply.text,
                    replyMediaDetails = reply.media,

                    commentId = comment.commentId,
                    commentUserId = comment.userId,
                    commentText = comment.text,
                    commentMediaDetails = comment.media,

                    postId = post.postId,
                    postType = post.postType,
                    postUserId = post.userId,
                    postMediaDetails = post.media,
                    postTitle = post.title,
                    postDescription = post.description,
                    userActivityType = userActivityType,
                )
                else -> error("Incorrect User Activity Method called for postId: ${post.postId}, commentId: ${comment.commentId}, replyId: ${reply.replyId} for userActivityType: $userActivityType")
            }
            asyncSaveUserActivity(UserActivity(
                userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                createdAt = DateUtils.getInstantNow(),
                userAggregateActivityType = userActivityType.toUserAggregateActivityType(),
                userActivityType = userActivityType,
                byUserId = byUserId,
                forUserId = forUserId,
                userActivityData = data.convertToString(),
            ))
        } catch (e: Exception) {
            logger.error("Saving UserActivity for postId: ${post.postId}, commentId: ${comment.commentId}, replyId: ${reply.replyId} for userActivityType: $userActivityType failed.")
            e.printStackTrace()
        }
    }

    fun saveUserLevelActivity(byUser: UserV2, forUser: UserV2, userActivityType: UserActivityType) {
        try {
            val data = when (userActivityType) {
                UserActivityType.USER_FOLLOWED,
                UserActivityType.USER_CLICKED_CONNECT,
                UserActivityType.USER_PROFILE_SHARED -> UserLevelUserActivity(
                    activityByUserId = byUser.userId,
                    forUserId = forUser.userId,
                    userActivityType = userActivityType,
                )
                else -> error("Incorrect User Activity Method called for byUserId: ${byUser.userId}, forUserId: ${forUser.userId} for userActivityType: $userActivityType")
            }
            asyncSaveUserActivity(UserActivity(
                userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                createdAt = DateUtils.getInstantNow(),
                userAggregateActivityType = userActivityType.toUserAggregateActivityType(),
                userActivityType = userActivityType,
                byUserId = byUser.userId,
                forUserId = forUser.userId,
                userActivityData = data.convertToString(),
            ))
        } catch (e: Exception) {
            logger.error("Saving UserActivity byUserId: ${byUser.userId}, forUserId: ${forUser.userId} for userActivityType: $userActivityType failed.")
            e.printStackTrace()
        }
    }


    fun saveLikeLevelActivity(like: Like) {
        GlobalScope.launch {
            logger.info("Later:Start: User Activity Like processing for likeId: ${like.likeId}")
            val userActivityFuture = async {
                when (like.resourceType) {
                    ResourceType.POST,
                    ResourceType.WALL -> {
                        val post = postProvider.getPost(like.resourceId) ?: error("Failed to get post data for postId: ${like.resourceId}")
                        savePostActivity(
                            like.userId,
                            post.userId,
                            post,
                            if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_LIKED else UserActivityType.WALL_LIKED
                        )
                    }

                    ResourceType.POST_COMMENT,
                    ResourceType.WALL_COMMENT -> {
                        val comment = commentProvider.getComment(like.resourceId) ?: error("Failed to get comment data for commentId: ${like.resourceId}")
                        val post = postProvider.getPost(comment.postId) ?: error("Failed to get post data for postId: ${comment.postId}")
                        saveCommentActivity(
                            like.userId,
                            comment.userId,
                            post,
                            comment,
                            if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_COMMENT_LIKED else UserActivityType.WALL_COMMENT_LIKED
                        )
                    }

                    ResourceType.POST_COMMENT_REPLY,
                    ResourceType.WALL_COMMENT_REPLY -> {
                        val reply = replyProvider.getCommentReply(like.resourceId) ?: error("Failed to get reply data for replyId: ${like.resourceId}")
                        val comment = commentProvider.getComment(reply.commentId) ?: error("Failed to get comment data for commentId: ${reply.commentId}")
                        val post = postProvider.getPost(comment.postId) ?: error("Failed to get post data for postId: ${reply.postId}")
                        saveReplyActivity(
                            like.userId,
                            reply.userId,
                            post,
                            comment,
                            reply,
                            if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_COMMENT_REPLY_LIKED else UserActivityType.WALL_COMMENT_REPLY_LIKED
                        )
                    }
                }
            }
            userActivityFuture.await()
            logger.info("Later:Done: User Activity Like processing for likeId: ${like.likeId}")
        }
    }

    fun saveBookmarkLevelActivity(bookmark: Bookmark) {
        GlobalScope.launch {
            logger.info("Later:Start: User Activity Bookmark processing for bookmarkId: ${bookmark.bookmarkId}")
            val userActivityFuture = async {
                when (bookmark.resourceType) {
                    ResourceType.POST,
                    ResourceType.WALL -> {
                        val post = postProvider.getPost(bookmark.resourceId) ?: error("Failed to get post data for postId: ${bookmark.resourceId}")
                        savePostActivity(
                            bookmark.userId,
                            post.userId,
                            post,
                            if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_SAVED else UserActivityType.WALL_SAVED
                        )
                    }
                    else -> error("Incorrect User Activity Method called for bookmarkId: ${bookmark.bookmarkId}, resourceType: ${bookmark.resourceType}")
                }
            }
            userActivityFuture.await()
            logger.info("Later:Done: User Activity Bookmark processing for bookmarkId: ${bookmark.bookmarkId}")
        }
    }

}
