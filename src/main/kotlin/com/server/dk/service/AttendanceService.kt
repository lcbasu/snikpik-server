package com.server.dk.service

import com.server.dk.dto.*

abstract class AttendanceService {
    abstract fun saveAttendance(saveAttendanceRequest: SaveAttendanceRequest): SavedAttendanceResponse?
    abstract fun getAttendanceInfo(attendanceInfoRequest: AttendanceInfoRequest): AttendanceInfoResponse?
    abstract fun markAttendance(markAttendanceRequest: MarkAttendanceRequest): SavedAttendanceByAdminResponse?
    abstract fun getAttendanceSummary(attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse?
    abstract fun getAttendanceReportForEmployee(employeeId: String, forDate: String): AttendanceReportForEmployeeResponse?
    abstract fun getAttendanceSummaryForEmployee(attendanceSummaryForEmployeeRequest: AttendanceSummaryForEmployeeRequest): AttendanceSummaryForEmployeeResponse?
}
