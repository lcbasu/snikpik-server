package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PaymentRepository : JpaRepository<Payment?, Long?> {
    @Query(value ="SELECT * FROM payment WHERE convert(for_date, datetime) >= :startDate and convert(for_date, datetime) <= :endDate and company_id = :companyId", nativeQuery = true)
    fun getAllPaymentsBetweenGivenDates(
        @Param("companyId") companyId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
    ): List<Payment>

}
