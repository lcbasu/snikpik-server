package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.ExtraChargeTax
import com.dukaankhata.server.entities.ExtraChargeTaxKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraChargeTaxRepository : JpaRepository<ExtraChargeTax?, ExtraChargeTaxKey?> {
    fun findAllByCompany(company: Company): List<ExtraChargeTax>
}
