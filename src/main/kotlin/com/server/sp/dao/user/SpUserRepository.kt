package com.server.sp.dao.user

import com.server.sp.entities.user.SpUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface SpUserRepository : CassandraRepository<SpUser?, String?> {
    fun findAllByUserId(userId: String): List<SpUser>
    fun findAllBy(pageable: Pageable): Slice<SpUser>
}
