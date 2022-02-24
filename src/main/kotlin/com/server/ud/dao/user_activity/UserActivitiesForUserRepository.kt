package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivity
import com.server.ud.entities.user_activity.UserActivityForUser
import com.server.ud.enums.UserActivityType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesForUserRepository : CassandraRepository<UserActivityForUser?, String?> {
    fun findAllByForUserId(forUserId: String, pageable: Pageable): Slice<UserActivityForUser>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
    ): List<UserActivityForUser>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
        commentId: String,
    ): List<UserActivityForUser>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
        commentId: String,
        replyId: String,
    ): List<UserActivityForUser>


    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityType(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
    ): List<UserActivityForUser>

    @Query("SELECT * FROM user_activities_for_user where post_id = ?0 allow filtering")
    fun getAllByPostId(postId: String): List<UserActivityForUser>

    @Query("SELECT * FROM user_activities_for_user where comment_id = ?0 allow filtering")
    fun getAllByCommentId(postId: String): List<UserActivityForUser>

    @Query("SELECT * FROM user_activities_for_user where reply_id = ?0 allow filtering")
    fun getAllByReplyId(postId: String): List<UserActivityForUser>
}
