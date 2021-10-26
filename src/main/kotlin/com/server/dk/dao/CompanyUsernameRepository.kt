package com.server.dk.dao

import com.server.dk.entities.Company
import com.server.dk.entities.CompanyUsername
import com.server.common.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyUsernameRepository : JpaRepository<CompanyUsername?, String?> {
    fun findByCompany(company: Company): List<CompanyUsername>
    fun findByAddedBy(user: User): List<CompanyUsername>
}
