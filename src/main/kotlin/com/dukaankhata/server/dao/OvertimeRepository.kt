package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Overtime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OvertimeRepository : JpaRepository<Overtime?, Long?> {
    @Query(value ="SELECT * FROM overtime WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and company_id = :companyId", nativeQuery = true)
    fun getAllOvertimesBetweenGivenTimes(
        @Param("companyId") companyId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<Overtime>

    @Query(value ="SELECT * FROM overtime WHERE for_date = :forDate and company_id = :companyId", nativeQuery = true)
    fun getAllOvertimesForDate(
        @Param("companyId") companyId: Long,
        @Param("forDate") forDate: String
    ): List<Overtime>
}
