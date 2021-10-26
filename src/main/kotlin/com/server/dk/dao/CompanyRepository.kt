package com.server.dk.dao

import com.server.dk.entities.Company
import com.server.common.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CompanyRepository : JpaRepository<Company?, String?> {
    fun findByUser(user: User): List<Company>
    fun findByUsername(username: String): Company?
}
