package com.server.dk.dao

import com.server.dk.entities.Attendance
import com.server.dk.entities.Company
import com.server.dk.entities.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AttendanceRepository : JpaRepository<Attendance?, String?> {
    @Query(value = "SELECT a FROM Attendance a WHERE a.forDate IN :forDates and a.company = :company")
    fun getAttendanceByCompanyAndDates(
        @Param("company") company: Company,
        @Param("forDates") forDates: Set<String>
    ): List<Attendance>

    fun findByCompanyAndForDate(company: Company, forDate: String): List<Attendance>

    @Query(value ="SELECT * FROM attendance WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and company_id = :companyId", nativeQuery = true)
    fun getAllAttendancesBetweenGivenTimes(
        @Param("companyId") companyId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<Attendance>

    @Query(value ="SELECT * FROM attendance WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and employee_id = :employeeId", nativeQuery = true)
    fun getAttendancesForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<Attendance>

    @Query(value ="SELECT * FROM attendance WHERE for_date IN :datesList and employee_id = :employeeId", nativeQuery = true)
    fun getAttendancesForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("datesList") datesList: List<String>
    ): List<Attendance>

}
