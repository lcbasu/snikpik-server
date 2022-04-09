package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.*
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.UserAggregateActivityType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface UserActivitiesRepository : CassandraRepository<UserActivity?, String?> {

    fun findAllByUserActivityId(
        userActivityId: String,
    ): List<UserActivity>

    fun findAllByUserActivityIdAndUserAggregateActivityTypeAndCreatedAtAndUserActivityType(
        userActivityId: String,
        userAggregateActivityType: UserAggregateActivityType,
        createdAt: Instant,
        userActivityType: UserActivityType
    ): List<UserActivity>

    fun findAllByUserActivityIdAndUserAggregateActivityTypeAndCreatedAtAndUserActivityTypeAndByUserIdAndForUserIdAndPostId(
        userActivityId: String,
        userAggregateActivityType: UserAggregateActivityType,
        createdAt: Instant,
        userActivityType: UserActivityType,
        byUserId: String,
        forUserId: String,
        postId: String,
    ): List<UserActivity>

    fun findAllByUserActivityIdAndUserAggregateActivityTypeAndCreatedAtAndUserActivityTypeAndByUserIdAndForUserIdAndPostIdAndCommentId(
        userActivityId: String,
        userAggregateActivityType: UserAggregateActivityType,
        createdAt: Instant,
        userActivityType: UserActivityType,
        byUserId: String,
        forUserId: String,
        postId: String,
        commentId: String,
    ): List<UserActivity>

    fun findAllByUserActivityIdAndUserAggregateActivityTypeAndCreatedAtAndUserActivityTypeAndByUserIdAndForUserIdAndPostIdAndCommentIdAndReplyId(
        userActivityId: String,
        userAggregateActivityType: UserAggregateActivityType,
        createdAt: Instant,
        userActivityType: UserActivityType,
        byUserId: String,
        forUserId: String,
        postId: String,
        commentId: String,
        replyId: String,
    ): List<UserActivity>

//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//    ): List<UserActivity>
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//        commentId: String,
//    ): List<UserActivity>
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//        commentId: String,
//        replyId: String,
//    ): List<UserActivity>
//
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityType(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//    ): List<UserActivity>

//    @Query("SELECT * FROM user_activities where post_id = ?0 allow filtering")
//    fun getAllByPostId(postId: String): List<UserActivity>
//
//    @Query("SELECT * FROM user_activities where comment_id = ?0 allow filtering")
//    fun getAllByCommentId(postId: String): List<UserActivity>
//
//    @Query("SELECT * FROM user_activities where reply_id = ?0 allow filtering")
//    fun getAllByReplyId(postId: String): List<UserActivity>
}

@Repository
interface UserActivityByPostTrackerRepository : CassandraRepository<UserActivityByPostTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<UserActivityByPostTracker>
    fun findAllByPostIdAndUserAggregateActivityTypeAndUserActivityType(postId: String, userAggregateActivityType: UserAggregateActivityType, userActivityType: UserActivityType, pageable: Pageable): Slice<UserActivityByPostTracker>
}

@Repository
interface UserActivityByCommentTrackerRepository : CassandraRepository<UserActivityByCommentTracker?, String?> {
    fun findAllByCommentId(commentId: String, pageable: Pageable): Slice<UserActivityByCommentTracker>
    fun findAllByCommentIdAndUserAggregateActivityTypeAndUserActivityType(commentId: String, userAggregateActivityType: UserAggregateActivityType, userActivityType: UserActivityType, pageable: Pageable): Slice<UserActivityByCommentTracker>
}

@Repository
interface UserActivityByReplyTrackerRepository : CassandraRepository<UserActivityByReplyTracker?, String?> {
    fun findAllByReplyId(replyId: String, pageable: Pageable): Slice<UserActivityByReplyTracker>
    fun findAllByReplyIdAndUserAggregateActivityTypeAndUserActivityType(replyId: String, userAggregateActivityType: UserAggregateActivityType, userActivityType: UserActivityType, pageable: Pageable): Slice<UserActivityByReplyTracker>
}

@Repository
interface UserActivityByChatTrackerRepository : CassandraRepository<UserActivityByChatTracker?, String?> {
    fun findAllByChatId(chatId: String, pageable: Pageable): Slice<UserActivityByChatTracker>
    fun findAllByChatIdAndUserAggregateActivityTypeAndUserActivityType(chatId: String, userAggregateActivityType: UserAggregateActivityType, userActivityType: UserActivityType, pageable: Pageable): Slice<UserActivityByChatTracker>
}

