package com.server.shop.dao

import com.server.shop.entities.CompanyAddressKeyV3
import com.server.shop.entities.CompanyAddressV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyAddressV3Repository : JpaRepository<CompanyAddressV3?, CompanyAddressKeyV3?> {
    fun findAllByCompanyId(companyId: String): List<CompanyAddressV3>
}
