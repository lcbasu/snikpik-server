package com.server.ud.dao.auth

import com.server.ud.entities.auth.OtpValidation
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface OtpValidationRepository : CassandraRepository<OtpValidation?, String?> {
    fun findAllByAbsoluteMobile(absoluteMobile: String): List<OtpValidation>
}
