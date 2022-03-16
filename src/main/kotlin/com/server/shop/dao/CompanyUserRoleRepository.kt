package com.server.shop.dao

import com.server.shop.entities.CompanyUserRole
import com.server.shop.entities.CompanyUserRoleKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyUserRoleRepository : JpaRepository<CompanyUserRole?, CompanyUserRoleKey?> {
    fun findAllByCompanyId(companyId: String): List<CompanyUserRole>
}
