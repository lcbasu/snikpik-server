package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.AttendanceByAdmin
import com.dukaankhata.server.entities.AttendanceByAdminKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttendanceByAdminRepository : JpaRepository<AttendanceByAdmin?, AttendanceByAdminKey?>
