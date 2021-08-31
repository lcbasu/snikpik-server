package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.CompanyUsername
import com.dukaankhata.server.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyUsernameRepository : JpaRepository<CompanyUsername?, String?> {
    fun findByCompany(company: Company): List<CompanyUsername>
    fun findByAddedBy(user: User): List<CompanyUsername>
}
