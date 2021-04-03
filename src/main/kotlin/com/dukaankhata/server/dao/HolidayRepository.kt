package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Holiday
import com.dukaankhata.server.entities.HolidayKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface HolidayRepository : JpaRepository<Holiday?, HolidayKey?> {
    @Query(value ="SELECT * FROM holiday WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and company_id = :companyId", nativeQuery = true)
    fun getAllHolidaysBetweenGivenTimes(
        @Param("companyId") companyId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<Holiday>
}
