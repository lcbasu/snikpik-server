package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.AttendanceService
import com.dukaankhata.server.service.converter.AttendanceServiceConverter
import com.dukaankhata.server.utils.AttendanceUtils
import com.dukaankhata.server.utils.AuthUtils
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AttendanceServiceImpl : AttendanceService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var attendanceUtils: AttendanceUtils

    @Autowired
    private lateinit var attendanceServiceConverter: AttendanceServiceConverter

    override fun saveAttendance(saveAttendanceRequest: SaveAttendanceRequest): SavedAttendanceResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = saveAttendanceRequest.companyId,
            employeeId = saveAttendanceRequest.employeeId,
            requiredRoleTypes = authUtils.allAccessRoles()
        )
        val attendance = attendanceUtils.saveAttendance(requestContext, saveAttendanceRequest)
        return attendanceServiceConverter.getSavedAttendanceResponse(attendance)
    }

    override fun getAttendanceInfo(attendanceInfoRequest: AttendanceInfoRequest): AttendanceInfoResponse? {
        return runBlocking {
            val requestContext = authUtils.validateRequest(
                companyId = attendanceInfoRequest.companyId,
                requiredRoleTypes = authUtils.allAccessRoles()
            )
            attendanceUtils.getAttendanceInfoV2(requestContext.company!!, attendanceInfoRequest.forDate)
        }
    }

    override fun markAttendance(markAttendanceRequest: MarkAttendanceRequest): SavedAttendanceByAdminResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = markAttendanceRequest.employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val attendance = attendanceUtils.markAttendance(requestContext, markAttendanceRequest)
        return attendanceServiceConverter.getSavedAttendanceByAdminResponse(attendance)
    }

    override fun getAttendanceSummary(attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = attendanceSummaryRequest.companyId,
            requiredRoleTypes = authUtils.allAccessRoles()
        )
        return attendanceUtils.getAttendanceSummary(requestContext, attendanceSummaryRequest)
    }

    override fun getAttendanceReportForEmployee(employeeId: Long, forDate: String): AttendanceReportForEmployeeResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        val employee = requestContext.employee!!

        return attendanceServiceConverter.getAttendanceReportForEmployeeResponse(attendanceUtils.getAttendanceReportForEmployee(employee, forDate))
    }

}
