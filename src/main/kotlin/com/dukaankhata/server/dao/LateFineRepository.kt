package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.LateFine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LateFineRepository : JpaRepository<LateFine?, String?> {
    @Query(value ="SELECT * FROM late_fine WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and company_id = :companyId", nativeQuery = true)
    fun getAllLateFineBetweenGivenTimes(
        @Param("companyId") companyId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<LateFine>

    @Query(value ="SELECT * FROM late_fine WHERE for_date = :forDate and company_id = :companyId", nativeQuery = true)
    fun getAllLateFineForDate(
        @Param("companyId") companyId: String,
        @Param("forDate") forDate: String
    ): List<LateFine>

    @Query(value ="SELECT * FROM late_fine WHERE for_date = :forDate and employee_id = :employeeId", nativeQuery = true)
    fun getLateFinesForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("forDate") forDate: String
    ): List<LateFine>

    @Query(value ="SELECT * FROM late_fine WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and employee_id = :employeeId", nativeQuery = true)
    fun getLateFinesForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<LateFine>

    @Query(value ="SELECT * FROM late_fine WHERE for_date IN :datesList and employee_id = :employeeId", nativeQuery = true)
    fun getLateFinesForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("datesList") datesList: List<String>
    ): List<LateFine>

}
