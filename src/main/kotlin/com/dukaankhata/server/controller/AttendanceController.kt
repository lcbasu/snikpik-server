package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.AttendanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("attendance")
class AttendanceController {
    @Autowired
    private lateinit var attendanceService: AttendanceService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody saveAttendanceRequest: SaveAttendanceRequest): SavedAttendanceResponse? {
        return attendanceService.saveAttendance(saveAttendanceRequest)
    }

    @RequestMapping(value = ["/getAttendances"], method = [RequestMethod.POST])
    fun getAttendances(@RequestBody getAttendancesRequest: GetAttendancesRequest): AttendancesResponse? {
        return attendanceService.getAttendances(getAttendancesRequest)
    }

    @RequestMapping(value = ["/getAttendanceInfo"], method = [RequestMethod.POST])
    fun getAttendanceInfo(@RequestBody attendanceInfoRequest: AttendanceInfoRequest): AttendanceInfoResponse? {
        return attendanceService.getAttendanceInfo(attendanceInfoRequest)
    }
}
