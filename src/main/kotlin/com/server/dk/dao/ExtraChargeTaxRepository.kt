package com.server.dk.dao

import com.server.dk.entities.Company
import com.server.dk.entities.ExtraChargeTax
import com.server.dk.entities.ExtraChargeTaxKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraChargeTaxRepository : JpaRepository<ExtraChargeTax?, ExtraChargeTaxKey?> {
    fun findAllByCompany(company: Company): List<ExtraChargeTax>
}
