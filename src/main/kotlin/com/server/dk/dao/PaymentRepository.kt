package com.server.dk.dao

import com.server.dk.entities.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PaymentRepository : JpaRepository<Payment?, String?> {
    @Query(value ="SELECT * FROM payment WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and company_id = :companyId", nativeQuery = true)
    fun getAllPaymentsBetweenGivenTimes(
        @Param("companyId") companyId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<Payment>

    @Query(value ="SELECT * FROM payment WHERE for_date = :forDate and employee_id = :employeeId", nativeQuery = true)
    fun getPaymentsForDate(
        @Param("employeeId") employeeId: String,
        @Param("forDate") forDate: String
    ): List<Payment>

    @Query(value ="SELECT * FROM payment WHERE for_date IN :datesList and company_id = :companyId", nativeQuery = true)
    fun getPayments(
        @Param("companyId") companyId: String,
        @Param("datesList") datesList: List<String>
    ): List<Payment>

    @Query(value ="SELECT * FROM payment WHERE for_date IN :datesList and employee_id = :employeeId", nativeQuery = true)
    fun getPaymentsForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("datesList") datesList: List<String>
    ): List<Payment>

}
