package com.server.dk.dao

import com.server.dk.entities.AttendanceByAdmin
import com.server.dk.entities.AttendanceByAdminKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AttendanceByAdminRepository : JpaRepository<AttendanceByAdmin?, AttendanceByAdminKey?> {
    @Query(value ="SELECT * FROM attendance_by_admin WHERE company_id = :companyId and convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime", nativeQuery = true)
    fun getAllAttendancesByAdminBetweenGivenTimes(
        @Param("companyId") companyId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<AttendanceByAdmin>

    @Query(value ="SELECT * FROM attendance_by_admin WHERE company_id = :companyId and for_date = :forDate", nativeQuery = true)
    fun getAllAttendancesByAdminForDate(
        @Param("companyId") companyId: String,
        @Param("forDate") forDate: String
    ): List<AttendanceByAdmin>

    @Query(value ="SELECT * FROM attendance_by_admin WHERE convert(for_date, datetime) >= :startTime and convert(for_date, datetime) <= :endTime and employee_id = :employeeId", nativeQuery = true)
    fun getAttendancesByAdminForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): List<AttendanceByAdmin>

    @Query(value ="SELECT * FROM attendance_by_admin WHERE for_date IN :datesList and employee_id = :employeeId", nativeQuery = true)
    fun getAttendancesByAdminForEmployee(
        @Param("employeeId") employeeId: String,
        @Param("datesList") datesList: List<String>,
    ): List<AttendanceByAdmin>
}
