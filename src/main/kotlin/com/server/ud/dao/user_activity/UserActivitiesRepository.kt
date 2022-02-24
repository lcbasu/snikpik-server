package com.server.ud.dao.user_activity

import com.server.ud.entities.post.NearbyPostsByZipcode
import com.server.ud.entities.user_activity.UserActivity
import com.server.ud.enums.UserActivityType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesRepository : CassandraRepository<UserActivity?, String?> {
    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
    ): List<UserActivity>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
        commentId: String,
    ): List<UserActivity>

    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityTypeAndPostIdAndCommentIdAndReplyId(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
        postId: String,
        commentId: String,
        replyId: String,
    ): List<UserActivity>


    @AllowFiltering
    fun findAllByByUserIdAndForUserIdAndUserActivityType(
        byUserId: String,
        forUserId: String,
        userActivityType: UserActivityType,
    ): List<UserActivity>

    @Query("SELECT * FROM user_activities where post_id = ?0 allow filtering")
    fun getAllByPostId(postId: String): List<UserActivity>

    @Query("SELECT * FROM user_activities where comment_id = ?0 allow filtering")
    fun getAllByCommentId(postId: String): List<UserActivity>

    @Query("SELECT * FROM user_activities where reply_id = ?0 allow filtering")
    fun getAllByReplyId(postId: String): List<UserActivity>
}
