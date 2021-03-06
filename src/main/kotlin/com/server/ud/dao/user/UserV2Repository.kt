package com.server.ud.dao.user

import com.server.ud.entities.user.UserV2
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserV2Repository : CassandraRepository<UserV2?, String?> {
    fun findAllByUserId(userId: String): List<UserV2>
    fun findAllBy(pageable: Pageable): Slice<UserV2>
}
