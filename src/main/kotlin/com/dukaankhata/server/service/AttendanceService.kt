package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class AttendanceService {
    abstract fun saveAttendance(saveAttendanceRequest: SaveAttendanceRequest): SavedAttendanceResponse?
    abstract fun getAttendances(getAttendancesRequest: GetAttendancesRequest): AttendancesResponse?
    abstract fun getAttendanceInfo(attendanceInfoRequest: AttendanceInfoRequest): AttendanceInfoResponse?
}
