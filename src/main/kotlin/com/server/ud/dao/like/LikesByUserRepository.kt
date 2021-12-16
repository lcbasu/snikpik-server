package com.server.ud.dao.like

import com.server.ud.entities.like.LikesByUser
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LikesByUserRepository : CassandraRepository<LikesByUser?, String?> {
    @AllowFiltering
    fun findAllByResourceId(resourceId: String): List<LikesByUser>
}
