package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivity
import com.server.ud.entities.user_activity.UserActivityByUser
import com.server.ud.enums.UserActivityType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesByUserRepository : CassandraRepository<UserActivityByUser?, String?> {
    fun findAllByByUserId(byUserId: String, pageable: Pageable): Slice<UserActivityByUser>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
    ): List<UserActivityByUser>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
        commentId: String,
    ): List<UserActivityByUser>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
        commentId: String,
        replyId: String,
    ): List<UserActivityByUser>


    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityType(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
    ): List<UserActivityByUser>


    @Query("SELECT * FROM user_activities_by_user where post_id = ?0 allow filtering")
    fun getAllByPostId(postId: String): List<UserActivityByUser>

    @Query("SELECT * FROM user_activities_by_user where comment_id = ?0 allow filtering")
    fun getAllByCommentId(postId: String): List<UserActivityByUser>

    @Query("SELECT * FROM user_activities_by_user where reply_id = ?0 allow filtering")
    fun getAllByReplyId(postId: String): List<UserActivityByUser>
}
