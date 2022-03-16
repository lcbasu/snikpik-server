package com.server.shop.dao

import com.server.shop.entities.CompanyPolicy
import com.server.shop.entities.CompanyPolicyKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyPolicyRepository : JpaRepository<CompanyPolicy?, CompanyPolicyKey?> {
    fun findAllByCompanyId(companyId: String): List<CompanyPolicy>
}
