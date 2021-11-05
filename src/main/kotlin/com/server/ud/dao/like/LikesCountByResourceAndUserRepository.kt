package com.server.ud.dao.like

import com.server.ud.entities.like.LikesCountByResourceAndUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LikesCountByResourceAndUserRepository : CassandraRepository<LikesCountByResourceAndUser?, String?> {

    @Query("select * from likes_count_by_resource_and_user where resource_id = ?0 and user_id = ?1")
    fun findAllByResourceAndUserId(resourceId: String, userId: String): List<LikesCountByResourceAndUser>

    @Query("UPDATE likes_count_by_resource_and_user SET likes_count = likes_count + 1 where resource_id = ?0 and user_id = ?1")
    fun incrementLikes(resourceId: String, userId: String)

    @Query("UPDATE likes_count_by_resource_and_user SET likes_count = likes_count - 1 where resource_id = ?0 and user_id = ?1")
    fun decrementLikes(resourceId: String, userId: String)

    @Query("UPDATE likes_count_by_resource_and_user SET likes_count = ?2 where resource_id = ?0 and user_id = ?1")
    fun setLikesCount(resourceId: String, userId: String, value: Long)
}
