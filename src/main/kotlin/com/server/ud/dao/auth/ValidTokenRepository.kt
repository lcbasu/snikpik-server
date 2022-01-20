package com.server.ud.dao.auth

import com.server.ud.entities.auth.ValidToken
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface ValidTokenRepository : CassandraRepository<ValidToken?, String?> {
    fun findAllByToken(token: String): List<ValidToken>
}
