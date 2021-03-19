package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.AttendanceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AttendanceUtils {

    @Autowired
    private lateinit var attendanceRepository: AttendanceRepository

}
