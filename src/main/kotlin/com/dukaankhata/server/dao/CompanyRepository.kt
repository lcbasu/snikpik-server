package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CompanyRepository : JpaRepository<Company?, String?> {
    fun findByUser(user: User): List<Company>
    fun findByUsername(username: String): Company?
}
