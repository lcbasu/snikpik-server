package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Attendance
import com.dukaankhata.server.entities.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AttendanceRepository : JpaRepository<Attendance?, Long?> {
    @Query(value = "SELECT a FROM Attendance a WHERE a.forDate IN :forDates and a.company = :company")
    fun getAttendanceByCompanyAndDates(
        @Param("company") company: Company,
        @Param("forDates") forDates: Set<String>
    ): List<Attendance>

    fun findByCompanyAndForDate(company: Company, forDate: String): List<Attendance>
}
