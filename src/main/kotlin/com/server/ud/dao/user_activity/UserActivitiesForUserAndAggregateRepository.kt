package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivityForUserAndAggregate
import com.server.ud.enums.UserAggregateActivityType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface UserActivitiesForUserAndAggregateRepository : CassandraRepository<UserActivityForUserAndAggregate?, String?> {
    fun findAllByForUserIdAndUserAggregateActivityType(forUserId: String, userAggregateActivityType: UserAggregateActivityType, pageable: Pageable): Slice<UserActivityForUserAndAggregate>

    fun findAllByForUserIdAndUserAggregateActivityTypeAndCreatedAt(forUserId: String, userAggregateActivityType: UserAggregateActivityType, createdAt: Instant): List<UserActivityForUserAndAggregate>

//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//    ): List<UserActivityForUserAndAggregate>
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//        commentId: String,
//    ): List<UserActivityForUserAndAggregate>
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//        commentId: String,
//        replyId: String,
//    ): List<UserActivityForUserAndAggregate>
//
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityType(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//    ): List<UserActivityForUserAndAggregate>
//
//
//    @Query("SELECT * FROM user_activities_for_user_and_aggregate where post_id = ?0 allow filtering")
//    fun getAllByPostId(postId: String): List<UserActivityForUserAndAggregate>
//
//    @Query("SELECT * FROM user_activities_for_user_and_aggregate where comment_id = ?0 allow filtering")
//    fun getAllByCommentId(postId: String): List<UserActivityForUserAndAggregate>
//
//    @Query("SELECT * FROM user_activities_for_user_and_aggregate where reply_id = ?0 allow filtering")
//    fun getAllByReplyId(postId: String): List<UserActivityForUserAndAggregate>
}
