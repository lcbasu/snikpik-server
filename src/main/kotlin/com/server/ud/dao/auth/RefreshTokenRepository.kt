package com.server.ud.dao.auth

import com.server.ud.entities.auth.RefreshToken
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : CassandraRepository<RefreshToken?, String?> {
    fun findAllByLoginSequenceId(loginSequenceId: String): List<RefreshToken>
}
