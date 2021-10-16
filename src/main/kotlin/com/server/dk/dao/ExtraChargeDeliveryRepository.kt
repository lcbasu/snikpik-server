package com.server.dk.dao

import com.server.dk.entities.Company
import com.server.dk.entities.ExtraChargeDelivery
import com.server.dk.entities.ExtraChargeDeliveryKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraChargeDeliveryRepository : JpaRepository<ExtraChargeDelivery?, ExtraChargeDeliveryKey?> {
    fun findAllByCompany(company: Company): List<ExtraChargeDelivery>
}
