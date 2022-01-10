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
import com.server.ud.enums.*
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.notification.DeviceNotificationProvider
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
    private lateinit var deviceNotificationProvider: DeviceNotificationProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun savePostCreationActivity(post: Post) {
        val userActivityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_CREATED else UserActivityType.WALL_CREATED
        savePostActivity(post.userId, null, post, userActivityType)
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

    fun saveChatMessageCreationActivity(message: UserChatMessage) {
        GlobalScope.launch {
            try {
                asyncSaveUserActivity(UserActivity(
                    userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                    createdAt = DateUtils.getInstantNow(),
                    userAggregateActivityType = UserAggregateActivityType.MESSAGE_SENT_OR_RECEIVED,
                    userActivityType = UserActivityType.USER_SENT_CHAT_MESSAGE,
                    byUserId = message.senderUserId,
                    forUserId = message.receiverUserId,
                    chatId = message.chatId,
                    chatMessageId = message.messageId,
                    chatSenderUserId = message.senderUserId,
                    chatReceiverUserId = message.receiverUserId,
                    chatText = message.text,
                    chatMedia = message.media,
                    chatCategories = message.categories,
                    chatMessageLocationId = message.locationId,
                ))
            } catch (e: Exception) {
                logger.error("Saving UserActivity for messageId: ${message.messageId} failed.")
                e.printStackTrace()
            }
        }
    }

    fun saveUserLevelActivity(byUser: UserV2, forUser: UserV2, userActivityType: UserActivityType) {
        try {
            val activity = when (userActivityType) {
                UserActivityType.USER_FOLLOWED,
                UserActivityType.USER_CLICKED_CONNECT,
                UserActivityType.USER_PROFILE_SHARED -> UserActivity(
                    userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                    createdAt = DateUtils.getInstantNow(),
                    userAggregateActivityType = userActivityType.toUserAggregateActivityType(),
                    userActivityType = userActivityType,
                    byUserId = byUser.userId,
                    forUserId = forUser.userId,
                )
                else -> error("Incorrect User Activity Method called for byUserId: ${byUser.userId}, forUserId: ${forUser.userId} for userActivityType: $userActivityType")
            }
            asyncSaveUserActivity(activity)
        } catch (e: Exception) {
            logger.error("Saving UserActivity byUserId: ${byUser.userId}, forUserId: ${forUser.userId} for userActivityType: $userActivityType failed.")
            e.printStackTrace()
        }
    }

    fun deleteUserLevelActivity(byUser: UserV2, forUser: UserV2, userActivityType: UserActivityType) {
        try {
            when (userActivityType) {
                UserActivityType.USER_FOLLOWED,
                UserActivityType.USER_CLICKED_CONNECT,
                UserActivityType.USER_PROFILE_SHARED -> {

                    userActivitiesRepository.deleteAll(
                        userActivitiesRepository.findAllByByUserIdAndForUserIdAndUserActivityType(
                            byUser.userId,
                            forUser.userId,
                            userActivityType,
                        )
                    )

                    userActivitiesByUserRepository.deleteAll(
                        userActivitiesByUserRepository.findAllByByUserIdAndForUserIdAndUserActivityType(
                            byUser.userId,
                            forUser.userId,
                            userActivityType,
                        )
                    )

                    userActivitiesByUserAndAggregateRepository.deleteAll(
                        userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityType(
                            byUser.userId,
                            forUser.userId,
                            userActivityType,
                        )
                    )

                    userActivitiesForUserRepository.deleteAll(
                        userActivitiesForUserRepository.findAllByByUserIdAndForUserIdAndUserActivityType(
                            byUser.userId,
                            forUser.userId,
                            userActivityType,
                        )
                    )

                    userActivitiesForUserAndAggregateRepository.deleteAll(
                        userActivitiesForUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityType(
                            byUser.userId,
                            forUser.userId,
                            userActivityType,
                        )
                    )
                }
                else -> error("Incorrect User Activity Method called for byUserId: ${byUser.userId}, forUserId: ${forUser.userId} for userActivityType: $userActivityType")
            }
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

    fun deleteLikeLevelActivity(like: Like) {
        GlobalScope.launch {
            logger.info("Later:Start: User Activity Like processing for likeId: ${like.likeId}")
            val userActivityFuture = async {
                when (like.resourceType) {
                    ResourceType.POST,
                    ResourceType.WALL -> {
                        val post = postProvider.getPost(like.resourceId) ?: error("Failed to get post data for postId: ${like.resourceId}")
                        val activityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_LIKED else UserActivityType.WALL_LIKED
                        userActivitiesRepository.deleteAll(
                            userActivitiesRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )

                        userActivitiesByUserRepository.deleteAll(
                            userActivitiesByUserRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )

                        userActivitiesByUserAndAggregateRepository.deleteAll(
                            userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )

                        userActivitiesForUserRepository.deleteAll(
                            userActivitiesForUserRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )

                        userActivitiesForUserAndAggregateRepository.deleteAll(
                            userActivitiesForUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )
                    }

                    ResourceType.POST_COMMENT,
                    ResourceType.WALL_COMMENT -> {
                        val comment = commentProvider.getComment(like.resourceId) ?: error("Failed to get comment data for commentId: ${like.resourceId}")
                        val post = postProvider.getPost(comment.postId) ?: error("Failed to get post data for postId: ${comment.postId}")
                        val activityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_COMMENT_LIKED else UserActivityType.WALL_COMMENT_LIKED

                        userActivitiesRepository.deleteAll(
                            userActivitiesRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId
                            )
                        )

                        userActivitiesByUserRepository.deleteAll(
                            userActivitiesByUserRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId
                            )
                        )

                        userActivitiesByUserAndAggregateRepository.deleteAll(
                            userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId
                            )
                        )

                        userActivitiesForUserRepository.deleteAll(
                            userActivitiesForUserRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId
                            )
                        )

                        userActivitiesForUserAndAggregateRepository.deleteAll(
                            userActivitiesForUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId
                            )
                        )
                    }

                    ResourceType.POST_COMMENT_REPLY,
                    ResourceType.WALL_COMMENT_REPLY -> {
                        val reply = replyProvider.getCommentReply(like.resourceId) ?: error("Failed to get reply data for replyId: ${like.resourceId}")
                        val comment = commentProvider.getComment(reply.commentId) ?: error("Failed to get comment data for commentId: ${reply.commentId}")
                        val post = postProvider.getPost(comment.postId) ?: error("Failed to get post data for postId: ${reply.postId}")
                        val activityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_COMMENT_REPLY_LIKED else UserActivityType.WALL_COMMENT_REPLY_LIKED

                        userActivitiesRepository.deleteAll(
                            userActivitiesRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId,
                                reply.replyId
                            )
                        )

                        userActivitiesByUserRepository.deleteAll(
                            userActivitiesByUserRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId,
                                reply.replyId
                            )
                        )

                        userActivitiesByUserAndAggregateRepository.deleteAll(
                            userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId,
                                reply.replyId
                            )
                        )

                        userActivitiesForUserRepository.deleteAll(
                            userActivitiesForUserRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId,
                                reply.replyId
                            )
                        )

                        userActivitiesForUserAndAggregateRepository.deleteAll(
                            userActivitiesForUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
                                like.userId,
                                post.userId,
                                activityType,
                                post.postId,
                                comment.commentId,
                                reply.replyId
                            )
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

    fun deleteBookmarkLevelActivity(bookmark: Bookmark) {
        GlobalScope.launch {
            logger.info("Later:Start: User Activity Bookmark processing for bookmarkId: ${bookmark.bookmarkId}")
            try {
                when (bookmark.resourceType) {
                    ResourceType.POST,
                    ResourceType.WALL -> {
                        val post = postProvider.getPost(bookmark.resourceId) ?: error("Failed to get post data for postId: ${bookmark.resourceId}")
                        val activityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_SAVED else UserActivityType.WALL_SAVED
                        userActivitiesRepository.deleteAll(
                            userActivitiesRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                bookmark.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )

                        userActivitiesByUserRepository.deleteAll(
                            userActivitiesByUserRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                bookmark.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )

                        userActivitiesByUserAndAggregateRepository.deleteAll(
                            userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                bookmark.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )

                        userActivitiesForUserRepository.deleteAll(
                            userActivitiesForUserRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                bookmark.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )

                        userActivitiesForUserAndAggregateRepository.deleteAll(
                            userActivitiesForUserAndAggregateRepository.findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
                                bookmark.userId,
                                post.userId,
                                activityType,
                                post.postId
                            )
                        )
                    }
                    else -> error("Incorrect User Activity Method called for bookmarkId: ${bookmark.bookmarkId}, resourceType: ${bookmark.resourceType}")
                }
            } catch (e: Exception) {
                logger.error("Incorrect User Activity Method called for bookmarkId: ${bookmark.bookmarkId}, resourceType: ${bookmark.resourceType}")
                e.printStackTrace()
            }

            logger.info("Later:Done: User Activity Bookmark processing for bookmarkId: ${bookmark.bookmarkId}")
        }
    }

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
        if (userActivity.byUserId == userActivity.forUserId) {
            logger.info("No need to track own activities")
            return
        }
        GlobalScope.launch {
            userActivitiesRepository.save(userActivity)
            userActivity.getUserActivityByUser().let {
                userActivitiesByUserRepository.save(it!!)
            }
            userActivity.getUserActivityByUserAndAggregate().let {
                userActivitiesByUserAndAggregateRepository.save(it!!)
            }
            userActivity.getUserActivityForUser()?.let {
                userActivitiesForUserRepository.save(it)
            }
            userActivity.getUserActivityForUserAndAggregate().let {
                userActivitiesForUserAndAggregateRepository.save(it!!)
            }
            deviceNotificationProvider.sendNotification(userActivity)
        }
    }

    private fun savePostActivity(byUserId: String, forUserId: String?, post: Post, userActivityType: UserActivityType) {
        try {
            val activity = when (userActivityType) {
                UserActivityType.POST_CREATED,
                UserActivityType.POST_LIKED,
                UserActivityType.POST_SAVED,
                UserActivityType.POST_SHARED,
                UserActivityType.WALL_CREATED,
                UserActivityType.WALL_LIKED,
                UserActivityType.WALL_SAVED,
                UserActivityType.WALL_SHARED -> UserActivity(
                    userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                    createdAt = DateUtils.getInstantNow(),
                    userAggregateActivityType = userActivityType.toUserAggregateActivityType(),
                    userActivityType = userActivityType,
                    byUserId = byUserId,
                    forUserId = forUserId,
                    postId = post.postId,
                    postType = post.postType,
                    postUserId = post.userId,
                    postMediaDetails = post.media,
                    postTitle = post.title,
                    postDescription = post.description,
                )
                else -> error("Incorrect User Activity Method called for postId: ${post.postId} for userActivityType: $userActivityType")
            }
            asyncSaveUserActivity(activity)
        } catch (e: Exception) {
            logger.error("Saving UserActivity for postId: ${post.postId} for userActivityType: $userActivityType failed.")
            e.printStackTrace()
        }
    }

    private fun saveReplyActivity(byUserId: String, forUserId: String?, post: Post, comment: Comment, reply: Reply, userActivityType: UserActivityType) {
        try {
            val activity = when (userActivityType) {
                UserActivityType.REPLIED_TO_POST_COMMENT,
                UserActivityType.POST_COMMENT_REPLY_LIKED,
                UserActivityType.REPLIED_TO_WALL_COMMENT,
                UserActivityType.WALL_COMMENT_REPLY_LIKED -> UserActivity(
                    userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                    createdAt = DateUtils.getInstantNow(),
                    userAggregateActivityType = userActivityType.toUserAggregateActivityType(),
                    userActivityType = userActivityType,
                    byUserId = byUserId,
                    forUserId = forUserId,

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
                )
                else -> error("Incorrect User Activity Method called for postId: ${post.postId}, commentId: ${comment.commentId}, replyId: ${reply.replyId} for userActivityType: $userActivityType")
            }
            asyncSaveUserActivity(activity)
        } catch (e: Exception) {
            logger.error("Saving UserActivity for postId: ${post.postId}, commentId: ${comment.commentId}, replyId: ${reply.replyId} for userActivityType: $userActivityType failed.")
            e.printStackTrace()
        }
    }

    private fun saveCommentActivity(byUserId: String, forUserId: String?, post: Post, comment: Comment, userActivityType: UserActivityType) {
        try {
            val activity = when (userActivityType) {
                UserActivityType.COMMENTED_ON_POST,
                UserActivityType.POST_COMMENT_LIKED,
                UserActivityType.COMMENTED_ON_WALL,
                UserActivityType.WALL_COMMENT_LIKED -> UserActivity(
                    userActivityId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UAT),
                    createdAt = DateUtils.getInstantNow(),
                    userAggregateActivityType = userActivityType.toUserAggregateActivityType(),
                    userActivityType = userActivityType,
                    byUserId = byUserId,
                    forUserId = forUserId,

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
                )
                else -> error("Incorrect User Activity Method called for postId: ${post.postId}, commentId: ${comment.commentId} for userActivityType: $userActivityType")
            }
            asyncSaveUserActivity(activity)
        } catch (e: Exception) {
            logger.error("Saving UserActivity for postId: ${post.postId}, commentId: ${comment.commentId} for userActivityType: $userActivityType failed.")
            e.printStackTrace()
        }
    }

}
