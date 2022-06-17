package com.server.common.dao.auth

import com.server.common.entities.auth.ValidToken
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface ValidTokenRepository : CassandraRepository<ValidToken?, String?> {
    fun findAllByToken(token: String): List<ValidToken>
}
