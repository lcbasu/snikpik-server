package com.server.ud.dao.like

import com.server.ud.entities.like.LikesCountByResource
import com.server.ud.entities.like.LikesCountByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LikesCountByUserRepository : CassandraRepository<LikesCountByUser?, String?> {
    @Query("select * from likes_count_by_user where user_id = ?0")
    fun findAllByUserId(userId: String?): List<LikesCountByUser>

    @Query("UPDATE likes_count_by_user SET likes_count = likes_count + 1 WHERE user_id = ?0")
    fun incrementLikes(userId: String)

    @Query("UPDATE likes_count_by_user SET likes_count = likes_count - 1 WHERE user_id = ?0")
    fun decrementLikes(userId: String)

    @Query("UPDATE likes_count_by_user SET likes_count = ?1 WHERE user_id = ?0")
    fun setLikesCount(userId: String, value: Long)
}
