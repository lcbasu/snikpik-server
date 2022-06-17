package com.server.ud.provider.user_activity

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.user_activity.*
import com.server.ud.dto.ByUserActivitiesFeedRequest
import com.server.ud.dto.ForUserActivitiesFeedRequest
import com.server.ud.dto.UserActivitiesFeedResponse
import com.server.ud.dto.toUserActivityResponse
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.Post
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user_activity.*
import com.server.ud.enums.*
import com.server.common.pagination.CassandraPageV2
import com.server.ud.provider.cache.BlockedIDs
import com.server.ud.provider.cache.UDCacheProviderV2
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.notification.DeviceNotificationProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.reply.ReplyProvider
import com.server.common.utils.PaginationRequestUtil
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
    private lateinit var userActivityByPostTrackerRepository: UserActivityByPostTrackerRepository

    @Autowired
    private lateinit var userActivityByChatTrackerRepository: UserActivityByChatTrackerRepository

    @Autowired
    private lateinit var userActivityByCommentTrackerRepository: UserActivityByCommentTrackerRepository

    @Autowired
    private lateinit var userActivityByReplyTrackerRepository: UserActivityByReplyTrackerRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

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

    @Autowired
    private lateinit var udCacheProvider: UDCacheProviderV2

    @Autowired
    private lateinit var securityProvider: SecurityProvider

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
                    userActivityId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.UAT.name),
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
                    userActivityId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.UAT.name),
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


                    val userActivitiesByUser = getAllActivitiesByUser(byUser.userId)

                    userActivitiesByUser.map { userActivityByUser ->

                        // 1
                        val allUserActivities = userActivitiesRepository.findAllByUserActivityId(
                            userActivityByUser.userActivityId,
                        )
                        allUserActivities.chunked(10).map { chunk ->
                            userActivitiesRepository.deleteAll(chunk)
                        }

                        // 2
                        val alluserActivitiesByUser = userActivitiesByUserRepository.findAllByByUserIdAndCreatedAt(
                            userActivityByUser.byUserId,
                            userActivityByUser.createdAt,
                        )
                        alluserActivitiesByUser.chunked(10).map { chunk ->
                            userActivitiesByUserRepository.deleteAll(chunk)
                        }

                        // 3
                        val alluserActivitiesByUserAndAggregate = userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndUserAggregateActivityTypeAndCreatedAt(
                            userActivityByUser.byUserId,
                            userActivityByUser.userAggregateActivityType,
                            userActivityByUser.createdAt,
                        )
                        alluserActivitiesByUserAndAggregate.chunked(10).map { chunk ->
                            userActivitiesByUserAndAggregateRepository.deleteAll(chunk)
                        }


                        // 4
                        userActivityByUser.forUserId?.let {
                            val alluserActivitiesForUser = userActivitiesForUserRepository.findAllByForUserIdAndCreatedAt(
                                it,
                                userActivityByUser.createdAt,
                            )
                            alluserActivitiesForUser.chunked(10).map { chunk ->
                                userActivitiesForUserRepository.deleteAll(chunk)
                            }
                        }

                        // 5

                        userActivityByUser.forUserId?.let {
                            val all = userActivitiesForUserAndAggregateRepository.findAllByForUserIdAndUserAggregateActivityTypeAndCreatedAt(
                                it,
                                userActivityByUser.userAggregateActivityType,
                                userActivityByUser.createdAt,
                            )
                            all.chunked(10).map { chunk ->
                                userActivitiesForUserAndAggregateRepository.deleteAll(chunk)
                            }
                        }

                    }
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
                    ResourceType.LIVE_QUESTION -> {
                        logger.error("Like processing for live question is not implemented yet.")
                    }
                }
            }
            userActivityFuture.await()
            logger.info("Later:Done: User Activity Like processing for likeId: ${like.likeId}")
        }
    }

    fun getAllUserActivityByPostTracker(postId: String, activityType: UserActivityType? = null): List<UserActivityByPostTracker> {
        val limit = 10
        var pagingState = ""

        val trackedActivities = mutableListOf<UserActivityByPostTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val activities = activityType?.let {
            userActivityByPostTrackerRepository.findAllByPostIdAndUserAggregateActivityTypeAndUserActivityType(
                postId,
                activityType.toUserAggregateActivityType(),
                activityType,
                pageRequest as Pageable
            )
        } ?: userActivityByPostTrackerRepository.findAllByPostId(
            postId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(activities)
        trackedActivities.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = activityType?.let {
                userActivityByPostTrackerRepository.findAllByPostIdAndUserAggregateActivityTypeAndUserActivityType(
                    postId,
                    activityType.toUserAggregateActivityType(),
                    activityType,
                    nextPageRequest as Pageable
                )
            } ?: userActivityByPostTrackerRepository.findAllByPostId(
                postId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedActivities.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedActivities
    }

    fun getAllUserActivityByCommentTracker(commentId: String,
                                           activityType: UserActivityType? = null): List<UserActivityByCommentTracker> {
        val limit = 10
        var pagingState = ""

        val trackedActivities = mutableListOf<UserActivityByCommentTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val activities = activityType?.let {
            userActivityByCommentTrackerRepository.findAllByCommentIdAndUserAggregateActivityTypeAndUserActivityType(
                commentId,
                activityType.toUserAggregateActivityType(),
                activityType,
                pageRequest as Pageable
            )
        } ?: userActivityByCommentTrackerRepository.findAllByCommentId(
            commentId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(activities)
        trackedActivities.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextComments = activityType?.let {
                userActivityByCommentTrackerRepository.findAllByCommentIdAndUserAggregateActivityTypeAndUserActivityType(
                    commentId,
                    activityType.toUserAggregateActivityType(),
                    activityType,
                    nextPageRequest as Pageable
                )
            } ?: userActivityByCommentTrackerRepository.findAllByCommentId(
                commentId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextComments)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedActivities.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedActivities
    }

    fun getAllUserActivityByReplyTracker(replyId: String,
                                         activityType: UserActivityType? = null): List<UserActivityByReplyTracker> {
        val limit = 10
        var pagingState = ""

        val trackedActivities = mutableListOf<UserActivityByReplyTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val activities = activityType?.let {
            userActivityByReplyTrackerRepository.findAllByReplyIdAndUserAggregateActivityTypeAndUserActivityType(
                replyId,
                activityType.toUserAggregateActivityType(),
                activityType,
                pageRequest as Pageable
            )
        } ?: userActivityByReplyTrackerRepository.findAllByReplyId(
            replyId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(activities)
        trackedActivities.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextReplies = activityType?.let {
                userActivityByReplyTrackerRepository.findAllByReplyIdAndUserAggregateActivityTypeAndUserActivityType(
                    replyId,
                    activityType.toUserAggregateActivityType(),
                    activityType,
                    nextPageRequest as Pageable
                )
            } ?: userActivityByReplyTrackerRepository.findAllByReplyId(
                replyId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextReplies)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedActivities.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedActivities
    }

    fun getAllUserActivityByChatTracker(chatId: String, activityType: UserActivityType): List<UserActivityByChatTracker> {
        val limit = 10
        var pagingState = ""

        val trackedActivities = mutableListOf<UserActivityByChatTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val activities = userActivityByChatTrackerRepository.findAllByChatIdAndUserAggregateActivityTypeAndUserActivityType(
            chatId,
            activityType.toUserAggregateActivityType(),
            activityType,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(activities)
        trackedActivities.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextChats = userActivityByChatTrackerRepository.findAllByChatIdAndUserAggregateActivityTypeAndUserActivityType(
                chatId,
                activityType.toUserAggregateActivityType(),
                activityType,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextChats)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedActivities.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedActivities
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
                        deleteActivitiesForPost(post.postId, activityType)
                    }

                    ResourceType.POST_COMMENT,
                    ResourceType.WALL_COMMENT -> {

                        val comment = commentProvider.getComment(like.resourceId) ?: error("Failed to get comment data for commentId: ${like.resourceId}")
                        val post = postProvider.getPost(comment.postId) ?: error("Failed to get post data for postId: ${comment.postId}")
                        val activityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_COMMENT_LIKED else UserActivityType.WALL_COMMENT_LIKED
                        deleteCommentActivities(comment.commentId, activityType)
                    }

                    ResourceType.POST_COMMENT_REPLY,
                    ResourceType.WALL_COMMENT_REPLY -> {
                        val reply = replyProvider.getCommentReply(like.resourceId) ?: error("Failed to get reply data for replyId: ${like.resourceId}")
                        val comment = commentProvider.getComment(reply.commentId) ?: error("Failed to get comment data for commentId: ${reply.commentId}")
                        val post = postProvider.getPost(comment.postId) ?: error("Failed to get post data for postId: ${reply.postId}")
                        val activityType = if (post.postType == PostType.GENERIC_POST) UserActivityType.POST_COMMENT_REPLY_LIKED else UserActivityType.WALL_COMMENT_REPLY_LIKED

                        deleteReplyActivities(reply.replyId, activityType)
                    }
                    ResourceType.LIVE_QUESTION -> {
                        logger.error("User Activity Like processing for likeId: ${like.likeId} is not implemented for live questions")
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

                        deleteActivitiesForPost(post.postId, activityType)

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

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            deleteActivitiesForPost(postId)
        }
    }

    fun deleteCommentExpandedData(commentId: String) {
        GlobalScope.launch {
            deleteCommentActivities(commentId)
        }
    }

    fun deleteReplyExpandedData(replyId: String) {
        GlobalScope.launch {
            deleteReplyActivities(replyId)
        }
    }

    fun getActivitiesFeedForUser_Internal(request: ForUserActivitiesFeedRequest): UserActivitiesFeedResponse? {
        val result = getActivitiesFeedForUser(request)
        val userId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse()
        val blockedIds = userId?.let {
            udCacheProvider.getBlockedIds(userId)
        } ?: BlockedIDs()
        val activities = result.content?.filterNotNull()?.filterNot {
            it.byUserId in blockedIds.userIds || it.postId in blockedIds.postIds || it.byUserId in blockedIds.mutedUserIds
        }?.mapNotNull { it.toUserActivityResponse() } ?: emptyList()
        return UserActivitiesFeedResponse(
            activities = activities,
            count = activities.size,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    fun getActivitiesFeedByUser_Internal(request: ByUserActivitiesFeedRequest): UserActivitiesFeedResponse? {
        val result = getActivitiesFeedByUser(request)
        val userId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse()
        val blockedIds = userId?.let {
            udCacheProvider.getBlockedIds(userId)
        } ?: BlockedIDs()
        val activities = result.content?.filterNotNull()?.filterNot {
            it.forUserId in blockedIds.userIds || it.postId in blockedIds.postIds || it.forUserId in blockedIds.mutedUserIds
        }?.mapNotNull { it.toUserActivityResponse() } ?: emptyList()
        return UserActivitiesFeedResponse(
            activities = activities,
            count = activities.size,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    fun getAllActivitiesByUser(byUserId: String): List<UserActivityByUser> {
        val limit = 10
        var pagingState = ""

        val trackedUsers = mutableListOf<UserActivityByUser>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = userActivitiesByUserRepository.findAllByByUserId(
            byUserId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedUsers.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = userActivitiesByUserRepository.findAllByByUserId(
                byUserId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedUsers.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedUsers
    }

    private fun getAllActivitiesForUser(forUserId: String): List<UserActivityForUser> {
        val limit = 10
        var pagingState = ""

        val trackedUsers = mutableListOf<UserActivityForUser>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = userActivitiesForUserRepository.findAllByForUserId(
            forUserId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedUsers.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = userActivitiesForUserRepository.findAllByForUserId(
                forUserId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedUsers.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedUsers
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

            userActivity.getUserActivityForUserAndAggregate()?.let {
                userActivitiesForUserAndAggregateRepository.save(it)
            }

            userActivity.toUserActivityByPostTracker()?.let {
                userActivityByPostTrackerRepository.save(it)
            }

            userActivity.toUserActivityByChatTracker()?.let {
                userActivityByChatTrackerRepository.save(it)
            }

            userActivity.toUserActivityByCommentTracker()?.let {
                userActivityByCommentTrackerRepository.save(it)
            }

            userActivity.toUserActivityByReplyTracker()?.let {
                userActivityByReplyTrackerRepository.save(it)
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
                    userActivityId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.UAT.name),
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
                    userActivityId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.UAT.name),
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
                    userActivityId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.UAT.name),
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

    private fun deleteReplyActivities(
        replyId: String,
        activityType: UserActivityType? = null
    ): List<List<Unit>?> {
        val replyTrackedActivities = getAllUserActivityByReplyTracker(replyId, activityType)
        return replyTrackedActivities.map { replyTrackedActivity ->
            val one = userActivitiesRepository.findAllByUserActivityId(
                replyTrackedActivity.userActivityId,
            )
            one.chunked(10).map {
                userActivitiesRepository.deleteAll(it)
            }

            val two = userActivitiesByUserRepository.findAllByByUserIdAndCreatedAt(
                replyTrackedActivity.userActivityId,
                replyTrackedActivity.createdAt,
            )
            two.chunked(10).map {
                userActivitiesByUserRepository.deleteAll(it)
            }

            val three =
                userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndUserAggregateActivityTypeAndCreatedAt(
                    replyTrackedActivity.byUserId,
                    replyTrackedActivity.userAggregateActivityType,
                    replyTrackedActivity.createdAt,
                )
            three.chunked(10).map {
                userActivitiesByUserAndAggregateRepository.deleteAll(it)
            }

            replyTrackedActivity.forUserId?.let {
                val four = userActivitiesForUserRepository.findAllByForUserIdAndCreatedAt(
                    replyTrackedActivity.forUserId,
                    replyTrackedActivity.createdAt,
                )
                four.chunked(10).map {
                    userActivitiesForUserRepository.deleteAll(it)
                }

                val five =
                    userActivitiesForUserAndAggregateRepository.findAllByForUserIdAndUserAggregateActivityTypeAndCreatedAt(
                        replyTrackedActivity.forUserId,
                        replyTrackedActivity.userAggregateActivityType,
                        replyTrackedActivity.createdAt,
                    )
                five.chunked(10).map {
                    userActivitiesForUserAndAggregateRepository.deleteAll(it)
                }
            }
        }
    }

    private fun deleteCommentActivities(
        commentId: String,
        activityType: UserActivityType? = null
    ) {
        val commentTrackedActivities = getAllUserActivityByCommentTracker(commentId, activityType)
        commentTrackedActivities.map { commentTrackedActivity ->
            val one = userActivitiesRepository.findAllByUserActivityId(
                commentTrackedActivity.userActivityId,
            )
            one.chunked(10).map {
                userActivitiesRepository.deleteAll(it)
            }

            val two = userActivitiesByUserRepository.findAllByByUserIdAndCreatedAt(
                commentTrackedActivity.userActivityId,
                commentTrackedActivity.createdAt,
            )
            two.chunked(10).map {
                userActivitiesByUserRepository.deleteAll(it)
            }

            val three =
                userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndUserAggregateActivityTypeAndCreatedAt(
                    commentTrackedActivity.byUserId,
                    commentTrackedActivity.userAggregateActivityType,
                    commentTrackedActivity.createdAt,
                )
            three.chunked(10).map {
                userActivitiesByUserAndAggregateRepository.deleteAll(it)
            }

            commentTrackedActivity.forUserId?.let {
                val four = userActivitiesForUserRepository.findAllByForUserIdAndCreatedAt(
                    commentTrackedActivity.forUserId,
                    commentTrackedActivity.createdAt,
                )
                four.chunked(10).map {
                    userActivitiesForUserRepository.deleteAll(it)
                }

                val five =
                    userActivitiesForUserAndAggregateRepository.findAllByForUserIdAndUserAggregateActivityTypeAndCreatedAt(
                        commentTrackedActivity.forUserId,
                        commentTrackedActivity.userAggregateActivityType,
                        commentTrackedActivity.createdAt,
                    )
                five.chunked(10).map {
                    userActivitiesForUserAndAggregateRepository.deleteAll(it)
                }
            }
        }
    }

    private fun deleteActivitiesForPost(
        postId: String,
        activityType: UserActivityType? = null
    ) {
        val postTrackedActivities = getAllUserActivityByPostTracker(postId, activityType)

        postTrackedActivities.map { postTrackedActivity ->
            val one = userActivitiesRepository.findAllByUserActivityId(
                postTrackedActivity.userActivityId,
            )
            one.chunked(10).map {
                userActivitiesRepository.deleteAll(it)
            }

            val two = userActivitiesByUserRepository.findAllByByUserIdAndCreatedAt(
                postTrackedActivity.userActivityId,
                postTrackedActivity.createdAt,
            )
            two.chunked(10).map {
                userActivitiesByUserRepository.deleteAll(it)
            }

            val three =
                userActivitiesByUserAndAggregateRepository.findAllByByUserIdAndUserAggregateActivityTypeAndCreatedAt(
                    postTrackedActivity.byUserId,
                    postTrackedActivity.userAggregateActivityType,
                    postTrackedActivity.createdAt,
                )
            three.chunked(10).map {
                userActivitiesByUserAndAggregateRepository.deleteAll(it)
            }

            postTrackedActivity.forUserId?.let {
                val four = userActivitiesForUserRepository.findAllByForUserIdAndCreatedAt(
                    postTrackedActivity.forUserId,
                    postTrackedActivity.createdAt,
                )
                four.chunked(10).map {
                    userActivitiesForUserRepository.deleteAll(it)
                }

                val five =
                    userActivitiesForUserAndAggregateRepository.findAllByForUserIdAndUserAggregateActivityTypeAndCreatedAt(
                        postTrackedActivity.forUserId,
                        postTrackedActivity.userAggregateActivityType,
                        postTrackedActivity.createdAt,
                    )
                five.chunked(10).map {
                    userActivitiesForUserAndAggregateRepository.deleteAll(it)
                }
            }
        }
    }

}
