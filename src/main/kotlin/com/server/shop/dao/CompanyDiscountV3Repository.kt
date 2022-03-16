package com.server.shop.dao

import com.server.shop.entities.CompanyDiscountKeyV3
import com.server.shop.entities.CompanyDiscountV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyDiscountV3Repository : JpaRepository<CompanyDiscountV3?, CompanyDiscountKeyV3?> {
    fun findAllByCompanyId(companyId: String): List<CompanyDiscountV3>
}
