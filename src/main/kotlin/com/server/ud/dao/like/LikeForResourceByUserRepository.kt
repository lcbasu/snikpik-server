package com.server.ud.dao.like

import com.server.ud.entities.like.LikeForResourceByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LikeForResourceByUserRepository : CassandraRepository<LikeForResourceByUser?, String?> {

    @Query("select * from likes_count_by_resource_and_user where resource_id = ?0 and user_id = ?1")
    fun findAllByResourceAndUserId(resourceId: String, userId: String): List<LikeForResourceByUser>
}
