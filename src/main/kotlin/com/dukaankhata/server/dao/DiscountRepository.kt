package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Discount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DiscountRepository : JpaRepository<Discount?, String?> {
    @Query(value ="SELECT * FROM discount WHERE SYSDATE() >= start_at and SYSDATE() <= end_at and company_id = :companyId", nativeQuery = true)
    fun getActiveDiscounts(
        @Param("companyId") companyId: String
    ): List<Discount>
}
