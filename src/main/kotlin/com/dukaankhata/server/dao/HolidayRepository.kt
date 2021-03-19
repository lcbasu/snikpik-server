package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Holiday
import com.dukaankhata.server.entities.HolidayKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HolidayRepository : JpaRepository<Holiday?, HolidayKey?>
