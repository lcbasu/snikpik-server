package com.server.dk.dao

import com.server.dk.entities.Holiday
import com.server.dk.entities.HolidayKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface HolidayRepository : JpaRepository<Holiday?, HolidayKey?> {
    @Query(value ="SELECT * FROM holiday WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and company_id = :companyId", nativeQuery = true)
    fun getAllHolidaysBetweenGivenTimes(
        @Param("companyId") companyId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<Holiday>

    @Query(value ="SELECT * FROM holiday WHERE company_id = :companyId and for_date = :forDate", nativeQuery = true)
    fun getAllHolidaysForDate(
        @Param("companyId") companyId: String,
        @Param("forDate") forDate: String
    ): List<Holiday>

//    @Query(value ="SELECT * FROM holiday WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and employee_id = :employeeId", nativeQuery = true)
//    fun getHolidayForEmployee(
//        @Param("employeeId") employeeId: String,
//        @Param("startTime") startTime: LocalDateTime,
//        @Param("endTime") endTime: LocalDateTime,
//    ): List<Holiday>

    @Query(value ="SELECT * FROM holiday WHERE for_date IN :datesList and employee_id = :employeeId", nativeQuery = true)
    fun getHolidayForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("datesList") datesList: List<String>
    ): List<Holiday>
}
