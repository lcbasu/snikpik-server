package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.AttendanceService
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("attendance")
class AttendanceController {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var attendanceService: AttendanceService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveAttendance(@RequestBody saveAttendanceRequest: SaveAttendanceRequest): SavedAttendanceResponse? {
        return attendanceService.saveAttendance(saveAttendanceRequest)
    }

    @RequestMapping(value = ["/getAttendances"], method = [RequestMethod.POST])
    fun getAttendances(@RequestBody getAttendancesRequest: GetAttendancesRequest): AttendancesResponse? {
        return attendanceService.getAttendances(getAttendancesRequest)
    }

    @RequestMapping(value = ["/getAttendanceInfo"], method = [RequestMethod.POST])
    fun getAttendanceInfo(@RequestBody attendanceInfoRequest: AttendanceInfoRequest): AttendanceInfoResponse? {
        var response: AttendanceInfoResponse?
        val time = measureTimeMillis {
            response = attendanceService.getAttendanceInfo(attendanceInfoRequest)
        }
        logger.info("getAttendanceInfo took $time milliseconds to executre")
        return response
    }

    @RequestMapping(value = ["/mark"], method = [RequestMethod.POST])
    fun markAttendance(@RequestBody markAttendanceRequest: MarkAttendanceRequest): SavedAttendanceByAdminResponse? {
        return attendanceService.markAttendance(markAttendanceRequest)
    }

    @RequestMapping(value = ["/getAttendanceSummary"], method = [RequestMethod.POST])
    fun getAttendanceSummary(@RequestBody attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse? {
        return attendanceService.getAttendanceSummary(attendanceSummaryRequest)
    }
}
