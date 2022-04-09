package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivityByUserAndAggregate
import com.server.ud.enums.UserAggregateActivityType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface UserActivitiesByUserAndAggregateRepository : CassandraRepository<UserActivityByUserAndAggregate?, String?> {
    fun findAllByByUserIdAndUserAggregateActivityType(byUserId: String, userAggregateActivityType: UserAggregateActivityType, pageable: Pageable): Slice<UserActivityByUserAndAggregate>

    fun findAllByByUserIdAndUserAggregateActivityTypeAndCreatedAt(
        byUserId: String,
        userAggregateActivityType: UserAggregateActivityType,
        createdAt: Instant): List<UserActivityByUserAndAggregate>

//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//    ): List<UserActivityByUserAndAggregate>
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//        commentId: String,
//    ): List<UserActivityByUserAndAggregate>
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//        postId: String,
//        commentId: String,
//        replyId: String,
//    ): List<UserActivityByUserAndAggregate>
//
//    @AllowFiltering
//    fun findAllByByUserIdAndForUserIdAndUserActivityType(
//        byUserId: String,
//        forUserId: String,
//        userActivityType: UserActivityType,
//    ): List<UserActivityByUserAndAggregate>


//    @Query("SELECT * FROM user_activities_by_user_and_aggregate where post_id = ?0 allow filtering")
//    fun getAllByPostId(postId: String): List<UserActivityByUserAndAggregate>
//
//    @Query("SELECT * FROM user_activities_by_user_and_aggregate where comment_id = ?0 allow filtering")
//    fun getAllByCommentId(postId: String): List<UserActivityByUserAndAggregate>
//
//    @Query("SELECT * FROM user_activities_by_user_and_aggregate where reply_id = ?0 allow filtering")
//    fun getAllByReplyId(postId: String): List<UserActivityByUserAndAggregate>
}
