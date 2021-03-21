package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Overtime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OvertimeRepository : JpaRepository<Overtime?, Long?>
