package com.server.dk.service.converter

import AttendanceInfoData
import AttendanceReportForEmployee
import com.server.dk.dto.*
import com.server.dk.entities.Company
import com.server.dk.entities.Employee
import com.server.dk.enums.AttendanceType
import com.server.dk.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AttendanceServiceConverter {

    fun getEmployeeAttendanceResponse(employee: Employee,
                                      workingMinutes: Int,
                                      attendanceType: AttendanceType,
                                      forDate: String,
                                      metaData: List<AttendanceUnit> = emptyList()): EmployeeAttendanceResponse {
        return EmployeeAttendanceResponse(
            employee = employee.toSavedEmployeeResponse(),
            workingHoursInMinutes = workingMinutes,
            attendanceType = attendanceType,
            forDate = forDate,
            metaData = metaData
        )
    }

    fun getAttendanceTypeAggregateResponse(attendanceType: AttendanceType, count: Int): AttendanceTypeAggregateResponse {
        return AttendanceTypeAggregateResponse(
            attendanceType = attendanceType,
            count = count
        )
    }

    fun getAttendanceInfoResponse(company: Company, forDate: String, employeesAttendance: List<EmployeeAttendanceResponse>, attendanceTypeAggregate: List<AttendanceTypeAggregateResponse>): AttendanceInfoResponse {
        return AttendanceInfoResponse(
            company = company.toSavedCompanyResponse(),
            forDate = forDate,
            employeesAttendance = employeesAttendance,
            attendanceTypeAggregate = attendanceTypeAggregate,
        )
    }

    fun getAttendanceSummary(company: Company,
                             startTime: LocalDateTime,
                             endTime: LocalDateTime,
                             attendanceReportForEmployees: List<AttendanceReportForEmployee>): AttendanceSummaryResponse {
        return AttendanceSummaryResponse(
            company = company.toSavedCompanyResponse(),
            startTime = DateUtils.getEpoch(startTime),
            endTime = DateUtils.getEpoch(endTime),
            employeesReport = attendanceReportForEmployees.map {
                AttendanceReportForEmployeeResponse(
                    employee = it.employee.toSavedEmployeeResponse(),
                    startTime = DateUtils.getEpoch(startTime),
                    endTime = DateUtils.getEpoch(endTime),
                    totalDay = it.totalDay,
                    presentDays = it.presentDays,
                    absentDays = it.absentDays,
                    halfDays = it.halfDays,
                    paidHolidays = it.paidHolidays,
                    nonPaidHolidays = it.nonPaidHolidays,
                    overtimeMinutes = it.overtimeMinutes,
                    overtimeAmountInPaisa = it.overtimeAmountInPaisa,
                    lateFineMinutes = it.lateFineMinutes,
                    lateFineAmountInPaisa = it.lateFineAmountInPaisa,
                    companyWorkingMinutesPerDay = it.companyWorkingMinutesPerDay
                )
            }
        )
    }

    fun getAttendanceReportForEmployeeResponse(attendanceReportForEmployee: AttendanceReportForEmployee?): AttendanceReportForEmployeeResponse? {
        if (attendanceReportForEmployee == null) {
            return null
        }
        return AttendanceReportForEmployeeResponse(
            employee = attendanceReportForEmployee.employee.toSavedEmployeeResponse(),
            startTime = DateUtils.getEpoch(DateUtils.parseStandardDate(attendanceReportForEmployee.startDate)),
            endTime = DateUtils.getEpoch(DateUtils.parseStandardDate(attendanceReportForEmployee.endDate)),
            totalDay = attendanceReportForEmployee.totalDay,
            presentDays = attendanceReportForEmployee.presentDays,
            absentDays = attendanceReportForEmployee.absentDays,
            halfDays = attendanceReportForEmployee.halfDays,
            paidHolidays = attendanceReportForEmployee.paidHolidays,
            nonPaidHolidays = attendanceReportForEmployee.nonPaidHolidays,
            overtimeMinutes = attendanceReportForEmployee.overtimeMinutes,
            overtimeAmountInPaisa = attendanceReportForEmployee.overtimeAmountInPaisa,
            lateFineMinutes = attendanceReportForEmployee.lateFineMinutes,
            lateFineAmountInPaisa = attendanceReportForEmployee.lateFineAmountInPaisa,
            companyWorkingMinutesPerDay = attendanceReportForEmployee.companyWorkingMinutesPerDay
        )
    }

    fun getAttendanceSummaryForEmployeeResponse(employee: Employee, attendancesReport: Map<String, AttendanceInfoData>, attendanceTypeAggregate: List<AttendanceTypeAggregateResponse>): AttendanceSummaryForEmployeeResponse? {
        val attendancesReportResponse = mutableMapOf<String, AttendanceInfoDataResponse>()
        attendancesReport.map {
            attendancesReportResponse.put(it.key, getAttendanceInfoDataResponse(it.value))
        }
        return AttendanceSummaryForEmployeeResponse(
            employee = employee.toSavedEmployeeResponse(),
            attendancesReport = attendancesReportResponse,
            attendanceTypeAggregate = attendanceTypeAggregate
        )
    }

    fun getAttendanceInfoDataResponse(attendanceInfoData: AttendanceInfoData): AttendanceInfoDataResponse {
        return AttendanceInfoDataResponse(
            attendanceType = attendanceInfoData.attendanceType,
            displayText = attendanceInfoData.displayText,
            dateNumber = attendanceInfoData.dateNumber,
            dateText = attendanceInfoData.dateText,
        )
    }
}
