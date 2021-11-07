package com.server.ud.dao.like

import com.server.ud.entities.like.LikeForResourceByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LikeForResourceByUserRepository : CassandraRepository<LikeForResourceByUser?, String?> {

    @Query("select * from like_for_resource_by_user where resource_id = ?0 and user_id = ?1")
    fun findAllByResourceAndUserId(resourceId: String, userId: String): List<LikeForResourceByUser>
}
