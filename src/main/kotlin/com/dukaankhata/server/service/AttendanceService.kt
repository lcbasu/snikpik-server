package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class AttendanceService {
    abstract fun saveAttendance(saveAttendanceRequest: SaveAttendanceRequest): SavedAttendanceResponse?
    abstract fun getAttendanceInfo(attendanceInfoRequest: AttendanceInfoRequest): AttendanceInfoResponse?
    abstract fun markAttendance(markAttendanceRequest: MarkAttendanceRequest): SavedAttendanceByAdminResponse?
    abstract fun getAttendanceSummary(attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse?
    abstract fun getAttendanceReportForEmployee(employeeId: String, forDate: String): AttendanceReportForEmployeeResponse?
    abstract fun getAttendanceSummaryForEmployee(attendanceSummaryForEmployeeRequest: AttendanceSummaryForEmployeeRequest): AttendanceSummaryForEmployeeResponse?
}
