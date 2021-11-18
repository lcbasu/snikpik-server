package com.server.ud.dao.social

import com.server.ud.entities.social.FollowersByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface FollowersByUserRepository : CassandraRepository<FollowersByUser?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<FollowersByUser>
}
