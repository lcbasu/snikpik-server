package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.ExtraChargeDelivery
import com.dukaankhata.server.entities.ExtraChargeDeliveryKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraChargeDeliveryRepository : JpaRepository<ExtraChargeDelivery?, ExtraChargeDeliveryKey?> {
    fun findAllByCompany(company: Company): List<ExtraChargeDelivery>
}
