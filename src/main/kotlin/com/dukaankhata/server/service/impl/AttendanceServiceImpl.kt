package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.AttendanceService
import com.dukaankhata.server.service.converter.AttendanceServiceConverter
import com.dukaankhata.server.provider.AttendanceProvider
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.CacheProvider
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AttendanceServiceImpl : AttendanceService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var cacheProvider: CacheProvider

    @Autowired
    private lateinit var attendanceProvider: AttendanceProvider

    @Autowired
    private lateinit var attendanceServiceConverter: AttendanceServiceConverter

    override fun saveAttendance(saveAttendanceRequest: SaveAttendanceRequest): SavedAttendanceResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = saveAttendanceRequest.companyId,
            employeeId = saveAttendanceRequest.employeeId,
            requiredRoleTypes = authProvider.allAccessRoles()
        )
        val attendance = attendanceProvider.saveAttendance(requestContext, saveAttendanceRequest)
        return attendance?.toSavedAttendanceResponse()
    }

    override fun getAttendanceInfo(attendanceInfoRequest: AttendanceInfoRequest): AttendanceInfoResponse? {
        return runBlocking {
            val requestContext = authProvider.validateRequest(
                companyServerIdOrUsername = attendanceInfoRequest.companyId,
                requiredRoleTypes = authProvider.allAccessRoles()
            )
            attendanceProvider.getAttendanceInfoV2(requestContext.company!!, attendanceInfoRequest.forDate)
        }
    }

    override fun markAttendance(markAttendanceRequest: MarkAttendanceRequest): SavedAttendanceByAdminResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = markAttendanceRequest.employeeId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val attendance = attendanceProvider.markAttendance(requestContext, markAttendanceRequest)
        return attendance.toSavedAttendanceByAdminResponse()
    }

    override fun getAttendanceSummary(attendanceSummaryRequest: AttendanceSummaryRequest): AttendanceSummaryResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = attendanceSummaryRequest.companyId,
            requiredRoleTypes = authProvider.allAccessRoles()
        )
        return attendanceProvider.getAttendanceSummary(requestContext, attendanceSummaryRequest)
    }

    override fun getAttendanceReportForEmployee(employeeId: String, forDate: String): AttendanceReportForEmployeeResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = employeeId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )

        val employee = requestContext.employee ?: error("Employee is required")

        return attendanceServiceConverter.getAttendanceReportForEmployeeResponse(attendanceProvider.getAttendanceReportForEmployee(employee, forDate))
    }

    override fun getAttendanceSummaryForEmployee(attendanceSummaryForEmployeeRequest: AttendanceSummaryForEmployeeRequest): AttendanceSummaryForEmployeeResponse? {
        return runBlocking {
            val requestContext = authProvider.validateRequest(
                employeeId = attendanceSummaryForEmployeeRequest.employeeId,
                requiredRoleTypes = authProvider.onlyAdminLevelRoles()
            )
            val employee = requestContext.employee ?: error("Employee is required")

            val responseFromCache = cacheProvider.getAttendanceSummaryForEmployee(attendanceSummaryForEmployeeRequest).await()

            responseFromCache?.let {
                // Return the cached value
                logger.info("Retuning values for getAttendanceSummaryForEmployee from cache")
                it
            } ?: attendanceProvider.getAttendanceSummaryForEmployee(employee = employee, forYear = attendanceSummaryForEmployeeRequest.forYear, forMonth = attendanceSummaryForEmployeeRequest.forMonth)
        }
    }

}
