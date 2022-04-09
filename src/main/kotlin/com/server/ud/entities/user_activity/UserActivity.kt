package com.server.ud.entities.user_activity

import com.server.common.utils.DateUtils
import com.server.ud.enums.PostType
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.UserAggregateActivityType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("user_activities")
class UserActivity (

    @PrimaryKeyColumn(name = "user_activity_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val userActivityId: String,

    @PrimaryKeyColumn(name = "user_aggregate_activity_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val userAggregateActivityType: UserAggregateActivityType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val createdAt: Instant = DateUtils.getInstantNow(),

    @Column("user_activity_type")
    val userActivityType: UserActivityType,

    @Column("by_user_id")
    val byUserId: String,

    @Column("for_user_id")
    val forUserId: String?,

    @Column("reply_id")
    val replyId: String? = null,
    @Column("reply_user_id")
    val replyUserId: String? = null,
    @Column("reply_text")
    val replyText: String? = null,
    @Column("reply_media_details")
    val replyMediaDetails: String? = null,

    @Column("comment_id")
    val commentId: String? = null,
    @Column("comment_user_id")
    val commentUserId: String? = null,
    @Column("comment_text")
    val commentText: String? = null,
    @Column("comment_media_details")
    val commentMediaDetails: String? = null,

    @Column("post_id")
    val postId: String? = null,
    @Column("post_type")
    val postType: PostType? = null,
    @Column("post_user_id")
    val postUserId: String? = null,
    @Column("post_media_details")
    val postMediaDetails: String? = null,
    @Column("post_title")
    val postTitle: String? = null,
    @Column("post_description")
    val postDescription: String? = null,

    @Column("chat_id")
    val chatId: String? = null,

    @Column("chat_message_id")
    val chatMessageId: String? = null,

    @Column("chat_sender_user_id")
    val chatSenderUserId: String? = null,

    @Column("chat_receiver_user_id")
    val chatReceiverUserId: String? = null,

    @Column("chat_text")
    val chatText: String? = null,

    @Column("chat_media")
    val chatMedia: String? = null, // MediaDetailsV2

    @Column("chat_categories")
    val chatCategories: String? = null, //  List of AllCategoryV2Response

    @Column("chat_message_location_id")
    val chatMessageLocationId: String? = null,
)

@Table("user_activities_by_post_tracker")
class UserActivityByPostTracker (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val postId: String,

    @PrimaryKeyColumn(name = "user_aggregate_activity_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val userAggregateActivityType: UserAggregateActivityType,

    @PrimaryKeyColumn(name = "user_activity_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val userActivityType: UserActivityType,

    @PrimaryKeyColumn(name = "user_activity_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    val userActivityId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val createdAt: Instant,

    @Column("by_user_id")
    val byUserId: String,

    @Column("for_user_id")
    val forUserId: String?,

    @Column("reply_id")
    val replyId: String? = null,
    @Column("reply_user_id")
    val replyUserId: String? = null,
    @Column("reply_text")
    val replyText: String? = null,
    @Column("reply_media_details")
    val replyMediaDetails: String? = null,

    @Column("comment_id")
    val commentId: String? = null,
    @Column("comment_user_id")
    val commentUserId: String? = null,
    @Column("comment_text")
    val commentText: String? = null,
    @Column("comment_media_details")
    val commentMediaDetails: String? = null,

    @Column("post_type")
    val postType: PostType? = null,
    @Column("post_user_id")
    val postUserId: String? = null,
    @Column("post_media_details")
    val postMediaDetails: String? = null,
    @Column("post_title")
    val postTitle: String? = null,
    @Column("post_description")
    val postDescription: String? = null,

    @Column("chat_id")
    val chatId: String? = null,

    @Column("chat_message_id")
    val chatMessageId: String? = null,

    @Column("chat_sender_user_id")
    val chatSenderUserId: String? = null,

    @Column("chat_receiver_user_id")
    val chatReceiverUserId: String? = null,

    @Column("chat_text")
    val chatText: String? = null,

    @Column("chat_media")
    val chatMedia: String? = null, // MediaDetailsV2

    @Column("chat_categories")
    val chatCategories: String? = null, //  List of AllCategoryV2Response

    @Column("chat_message_location_id")
    val chatMessageLocationId: String? = null,
)

@Table("user_activities_by_chat_tracker")
class UserActivityByChatTracker (

    @PrimaryKeyColumn(name = "chat_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val chatId: String,

    @PrimaryKeyColumn(name = "user_aggregate_activity_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val userAggregateActivityType: UserAggregateActivityType,

    @PrimaryKeyColumn(name = "user_activity_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val userActivityType: UserActivityType,

    @PrimaryKeyColumn(name = "user_activity_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    val userActivityId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val createdAt: Instant,

    @Column("by_user_id")
    val byUserId: String,

    @Column("for_user_id")
    val forUserId: String?,

    @Column("reply_id")
    val replyId: String? = null,
    @Column("reply_user_id")
    val replyUserId: String? = null,
    @Column("reply_text")
    val replyText: String? = null,
    @Column("reply_media_details")
    val replyMediaDetails: String? = null,

    @Column("comment_id")
    val commentId: String? = null,
    @Column("comment_user_id")
    val commentUserId: String? = null,
    @Column("comment_text")
    val commentText: String? = null,
    @Column("comment_media_details")
    val commentMediaDetails: String? = null,

    @Column("post_id")
    val postId: String? = null,
    @Column("post_type")
    val postType: PostType? = null,
    @Column("post_user_id")
    val postUserId: String? = null,
    @Column("post_media_details")
    val postMediaDetails: String? = null,
    @Column("post_title")
    val postTitle: String? = null,
    @Column("post_description")
    val postDescription: String? = null,

    @Column("chat_message_id")
    val chatMessageId: String? = null,

    @Column("chat_sender_user_id")
    val chatSenderUserId: String? = null,

    @Column("chat_receiver_user_id")
    val chatReceiverUserId: String? = null,

    @Column("chat_text")
    val chatText: String? = null,

    @Column("chat_media")
    val chatMedia: String? = null, // MediaDetailsV2

    @Column("chat_categories")
    val chatCategories: String? = null, //  List of AllCategoryV2Response

    @Column("chat_message_location_id")
    val chatMessageLocationId: String? = null,
)

@Table("user_activities_by_comment_tracker")
class UserActivityByCommentTracker (

    @PrimaryKeyColumn(name = "comment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val commentId: String,

    @PrimaryKeyColumn(name = "user_aggregate_activity_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val userAggregateActivityType: UserAggregateActivityType,

    @PrimaryKeyColumn(name = "user_activity_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val userActivityType: UserActivityType,

    @PrimaryKeyColumn(name = "user_activity_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    val userActivityId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val createdAt: Instant,

    @Column("by_user_id")
    val byUserId: String,

    @Column("for_user_id")
    val forUserId: String?,

    @Column("reply_id")
    val replyId: String? = null,
    @Column("reply_user_id")
    val replyUserId: String? = null,
    @Column("reply_text")
    val replyText: String? = null,
    @Column("reply_media_details")
    val replyMediaDetails: String? = null,

    @Column("comment_user_id")
    val commentUserId: String? = null,
    @Column("comment_text")
    val commentText: String? = null,
    @Column("comment_media_details")
    val commentMediaDetails: String? = null,

    @Column("post_id")
    val postId: String? = null,
    @Column("post_type")
    val postType: PostType? = null,
    @Column("post_user_id")
    val postUserId: String? = null,
    @Column("post_media_details")
    val postMediaDetails: String? = null,
    @Column("post_title")
    val postTitle: String? = null,
    @Column("post_description")
    val postDescription: String? = null,

    @Column("chat_id")
    val chatId: String? = null,

    @Column("chat_message_id")
    val chatMessageId: String? = null,

    @Column("chat_sender_user_id")
    val chatSenderUserId: String? = null,

    @Column("chat_receiver_user_id")
    val chatReceiverUserId: String? = null,

    @Column("chat_text")
    val chatText: String? = null,

    @Column("chat_media")
    val chatMedia: String? = null, // MediaDetailsV2

    @Column("chat_categories")
    val chatCategories: String? = null, //  List of AllCategoryV2Response

    @Column("chat_message_location_id")
    val chatMessageLocationId: String? = null,
)

@Table("user_activities_by_reply_tracker")
class UserActivityByReplyTracker (

    @PrimaryKeyColumn(name = "reply_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val replyId: String,

    @PrimaryKeyColumn(name = "user_aggregate_activity_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val userAggregateActivityType: UserAggregateActivityType,

    @PrimaryKeyColumn(name = "user_activity_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val userActivityType: UserActivityType,

    @PrimaryKeyColumn(name = "user_activity_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    val userActivityId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val createdAt: Instant,

    @Column("by_user_id")
    val byUserId: String,

    @Column("for_user_id")
    val forUserId: String?,

    @Column("reply_user_id")
    val replyUserId: String? = null,
    @Column("reply_text")
    val replyText: String? = null,
    @Column("reply_media_details")
    val replyMediaDetails: String? = null,

    @Column("comment_id")
    val commentId: String? = null,
    @Column("comment_user_id")
    val commentUserId: String? = null,
    @Column("comment_text")
    val commentText: String? = null,
    @Column("comment_media_details")
    val commentMediaDetails: String? = null,

    @Column("post_id")
    val postId: String? = null,
    @Column("post_type")
    val postType: PostType? = null,
    @Column("post_user_id")
    val postUserId: String? = null,
    @Column("post_media_details")
    val postMediaDetails: String? = null,
    @Column("post_title")
    val postTitle: String? = null,
    @Column("post_description")
    val postDescription: String? = null,

    @Column("chat_id")
    val chatId: String? = null,

    @Column("chat_message_id")
    val chatMessageId: String? = null,

    @Column("chat_sender_user_id")
    val chatSenderUserId: String? = null,

    @Column("chat_receiver_user_id")
    val chatReceiverUserId: String? = null,

    @Column("chat_text")
    val chatText: String? = null,

    @Column("chat_media")
    val chatMedia: String? = null, // MediaDetailsV2

    @Column("chat_categories")
    val chatCategories: String? = null, //  List of AllCategoryV2Response

    @Column("chat_message_location_id")
    val chatMessageLocationId: String? = null,
)

fun UserActivity.toUserActivityByPostTracker(): UserActivityByPostTracker? {
    this.apply {
        return postId?.let {
            UserActivityByPostTracker(
                userActivityId = userActivityId,
                userAggregateActivityType = userAggregateActivityType,
                userActivityType = userActivityType,
                createdAt = createdAt,
                byUserId = byUserId,
                forUserId = forUserId,

                replyId = replyId,
                replyUserId = replyUserId,
                replyText = replyText,
                replyMediaDetails = replyMediaDetails,

                commentId = commentId,
                commentUserId = commentUserId,
                commentText = commentText,
                commentMediaDetails = commentMediaDetails,

                postId = postId,
                postType = postType,
                postUserId = postUserId,
                postMediaDetails = postMediaDetails,
                postTitle = postTitle,
                postDescription = postDescription,

                chatId = chatId,
                chatMessageId = chatMessageId,
                chatSenderUserId = chatSenderUserId,
                chatReceiverUserId = chatReceiverUserId,
                chatText = chatText,
                chatMedia = chatMedia,
                chatCategories = chatCategories,
                chatMessageLocationId = chatMessageLocationId,
            )
        }
    }
}

fun UserActivity.toUserActivityByChatTracker(): UserActivityByChatTracker? {
    this.apply {
        return chatId?.let {
            UserActivityByChatTracker(
                userActivityId = userActivityId,
                userAggregateActivityType = userAggregateActivityType,
                userActivityType = userActivityType,
                createdAt = createdAt,
                byUserId = byUserId,
                forUserId = forUserId,

                replyId = replyId,
                replyUserId = replyUserId,
                replyText = replyText,
                replyMediaDetails = replyMediaDetails,

                commentId = commentId,
                commentUserId = commentUserId,
                commentText = commentText,
                commentMediaDetails = commentMediaDetails,

                postId = postId,
                postType = postType,
                postUserId = postUserId,
                postMediaDetails = postMediaDetails,
                postTitle = postTitle,
                postDescription = postDescription,

                chatId = chatId,
                chatMessageId = chatMessageId,
                chatSenderUserId = chatSenderUserId,
                chatReceiverUserId = chatReceiverUserId,
                chatText = chatText,
                chatMedia = chatMedia,
                chatCategories = chatCategories,
                chatMessageLocationId = chatMessageLocationId,
            )
        }
    }
}

fun UserActivity.toUserActivityByCommentTracker(): UserActivityByCommentTracker? {
    this.apply {
        return commentId?.let {
            UserActivityByCommentTracker(
                userActivityId = userActivityId,
                userAggregateActivityType = userAggregateActivityType,
                userActivityType = userActivityType,
                createdAt = createdAt,
                byUserId = byUserId,
                forUserId = forUserId,

                replyId = replyId,
                replyUserId = replyUserId,
                replyText = replyText,
                replyMediaDetails = replyMediaDetails,

                commentId = commentId,
                commentUserId = commentUserId,
                commentText = commentText,
                commentMediaDetails = commentMediaDetails,

                postId = postId,
                postType = postType,
                postUserId = postUserId,
                postMediaDetails = postMediaDetails,
                postTitle = postTitle,
                postDescription = postDescription,

                chatId = chatId,
                chatMessageId = chatMessageId,
                chatSenderUserId = chatSenderUserId,
                chatReceiverUserId = chatReceiverUserId,
                chatText = chatText,
                chatMedia = chatMedia,
                chatCategories = chatCategories,
                chatMessageLocationId = chatMessageLocationId,
            )
        }
    }
}

fun UserActivity.toUserActivityByReplyTracker(): UserActivityByReplyTracker? {
    this.apply {
        return replyId?.let {
            UserActivityByReplyTracker(
                userActivityId = userActivityId,
                userAggregateActivityType = userAggregateActivityType,
                userActivityType = userActivityType,
                createdAt = createdAt,
                byUserId = byUserId,
                forUserId = forUserId,

                replyId = replyId,
                replyUserId = replyUserId,
                replyText = replyText,
                replyMediaDetails = replyMediaDetails,

                commentId = commentId,
                commentUserId = commentUserId,
                commentText = commentText,
                commentMediaDetails = commentMediaDetails,

                postId = postId,
                postType = postType,
                postUserId = postUserId,
                postMediaDetails = postMediaDetails,
                postTitle = postTitle,
                postDescription = postDescription,

                chatId = chatId,
                chatMessageId = chatMessageId,
                chatSenderUserId = chatSenderUserId,
                chatReceiverUserId = chatReceiverUserId,
                chatText = chatText,
                chatMedia = chatMedia,
                chatCategories = chatCategories,
                chatMessageLocationId = chatMessageLocationId,
            )
        }
    }
}



fun UserActivity.getUserActivityByUser(): UserActivityByUser? {
    this.apply {
        return try {
            return UserActivityByUser(
                userActivityId = userActivityId,
                userAggregateActivityType = userAggregateActivityType,
                userActivityType = userActivityType,
                createdAt = createdAt,
                byUserId = byUserId,
                forUserId = forUserId,

                replyId = replyId,
                replyUserId = replyUserId,
                replyText = replyText,
                replyMediaDetails = replyMediaDetails,

                commentId = commentId,
                commentUserId = commentUserId,
                commentText = commentText,
                commentMediaDetails = commentMediaDetails,

                postId = postId,
                postType = postType,
                postUserId = postUserId,
                postMediaDetails = postMediaDetails,
                postTitle = postTitle,
                postDescription = postDescription,

                chatId = chatId,
                chatMessageId = chatMessageId,
                chatSenderUserId = chatSenderUserId,
                chatReceiverUserId = chatReceiverUserId,
                chatText = chatText,
                chatMedia = chatMedia,
                chatCategories = chatCategories,
                chatMessageLocationId = chatMessageLocationId,
            )
        } catch (e: Exception) {
            null
        }
    }
}

fun UserActivity.getUserActivityByUserAndAggregate(): UserActivityByUserAndAggregate? {
    this.apply {
        return try {
            return UserActivityByUserAndAggregate(
                userActivityId = userActivityId,
                userAggregateActivityType = userAggregateActivityType,
                userActivityType = userActivityType,
                createdAt = createdAt,
                byUserId = byUserId,
                forUserId = forUserId,

                replyId = replyId,
                replyUserId = replyUserId,
                replyText = replyText,
                replyMediaDetails = replyMediaDetails,

                commentId = commentId,
                commentUserId = commentUserId,
                commentText = commentText,
                commentMediaDetails = commentMediaDetails,

                postId = postId,
                postType = postType,
                postUserId = postUserId,
                postMediaDetails = postMediaDetails,
                postTitle = postTitle,
                postDescription = postDescription,

                chatId = chatId,
                chatMessageId = chatMessageId,
                chatSenderUserId = chatSenderUserId,
                chatReceiverUserId = chatReceiverUserId,
                chatText = chatText,
                chatMedia = chatMedia,
                chatCategories = chatCategories,
                chatMessageLocationId = chatMessageLocationId,
            )
        } catch (e: Exception) {
            null
        }
    }
}

fun UserActivity.getUserActivityForUser(): UserActivityForUser? {
    this.apply {
        return try {
            return forUserId?.let {
                UserActivityForUser(
                    userActivityId = userActivityId,
                    userAggregateActivityType = userAggregateActivityType,
                    userActivityType = userActivityType,
                    createdAt = createdAt,
                    byUserId = byUserId,
                    forUserId = it,

                    replyId = replyId,
                    replyUserId = replyUserId,
                    replyText = replyText,
                    replyMediaDetails = replyMediaDetails,

                    commentId = commentId,
                    commentUserId = commentUserId,
                    commentText = commentText,
                    commentMediaDetails = commentMediaDetails,

                    postId = postId,
                    postType = postType,
                    postUserId = postUserId,
                    postMediaDetails = postMediaDetails,
                    postTitle = postTitle,
                    postDescription = postDescription,

                    chatId = chatId,
                    chatMessageId = chatMessageId,
                    chatSenderUserId = chatSenderUserId,
                    chatReceiverUserId = chatReceiverUserId,
                    chatText = chatText,
                    chatMedia = chatMedia,
                    chatCategories = chatCategories,
                    chatMessageLocationId = chatMessageLocationId,
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}

fun UserActivity.getUserActivityForUserAndAggregate(): UserActivityForUserAndAggregate? {
    this.apply {
        return try {
            return forUserId?.let {
                UserActivityForUserAndAggregate(
                    userActivityId = userActivityId,
                    userAggregateActivityType = userAggregateActivityType,
                    userActivityType = userActivityType,
                    createdAt = createdAt,
                    byUserId = byUserId,
                    forUserId = it,

                    replyId = replyId,
                    replyUserId = replyUserId,
                    replyText = replyText,
                    replyMediaDetails = replyMediaDetails,

                    commentId = commentId,
                    commentUserId = commentUserId,
                    commentText = commentText,
                    commentMediaDetails = commentMediaDetails,

                    postId = postId,
                    postType = postType,
                    postUserId = postUserId,
                    postMediaDetails = postMediaDetails,
                    postTitle = postTitle,
                    postDescription = postDescription,

                    chatId = chatId,
                    chatMessageId = chatMessageId,
                    chatSenderUserId = chatSenderUserId,
                    chatReceiverUserId = chatReceiverUserId,
                    chatText = chatText,
                    chatMedia = chatMedia,
                    chatCategories = chatCategories,
                    chatMessageLocationId = chatMessageLocationId,
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
