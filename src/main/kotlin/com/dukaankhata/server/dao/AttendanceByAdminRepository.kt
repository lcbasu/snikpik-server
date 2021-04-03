package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.AttendanceByAdmin
import com.dukaankhata.server.entities.AttendanceByAdminKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AttendanceByAdminRepository : JpaRepository<AttendanceByAdmin?, AttendanceByAdminKey?> {
    @Query(value ="SELECT * FROM attendance_by_admin WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and company_id = :companyId", nativeQuery = true)
    fun getAllAttendancesByAdminBetweenGivenTimes(
        @Param("companyId") companyId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<AttendanceByAdmin>
}
