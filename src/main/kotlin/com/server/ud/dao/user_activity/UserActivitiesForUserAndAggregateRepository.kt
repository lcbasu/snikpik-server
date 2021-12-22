package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivityForUserAndAggregate
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.UserAggregateActivityType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesForUserAndAggregateRepository : CassandraRepository<UserActivityForUserAndAggregate?, String?> {
    fun findAllByForUserIdAndUserAggregateActivityType(forUserId: String, userAggregateActivityType: UserAggregateActivityType, pageable: Pageable): Slice<UserActivityForUserAndAggregate>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
    ): List<UserActivityForUserAndAggregate>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
        commentId: String,
    ): List<UserActivityForUserAndAggregate>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
        commentId: String,
        replyId: String,
    ): List<UserActivityForUserAndAggregate>


    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityType(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
    ): List<UserActivityForUserAndAggregate>
}
