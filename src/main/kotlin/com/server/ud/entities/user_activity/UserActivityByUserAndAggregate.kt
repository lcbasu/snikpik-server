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

@Table("user_activities_by_user_and_aggregate")
class UserActivityByUserAndAggregate (

    @PrimaryKeyColumn(name = "by_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var byUserId: String,

    @PrimaryKeyColumn(name = "user_aggregate_activity_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userAggregateActivityType: UserAggregateActivityType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("user_activity_type")
    var userActivityType: UserActivityType,

    @Column("for_user_id")
    var forUserId: String?,

    @Column("user_activity_id")
    var userActivityId: String,

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
)

fun UserActivityByUserAndAggregate.toUserActivityByUser(): UserActivityByUser {
    this.apply {
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
        )
    }
}
