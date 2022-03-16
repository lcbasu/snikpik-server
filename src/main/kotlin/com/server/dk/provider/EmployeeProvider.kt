package com.server.dk.provider

import AttendanceReportForEmployee
import EmployeeWorkingDetailsForMonthWithDate
import com.server.common.enums.ReadableIdPrefix
import com.server.dk.dao.EmployeeRepository
import com.server.dk.dto.RemoveEmployeeRequest
import com.server.dk.dto.SalarySlipResponse
import com.server.dk.dto.SaveEmployeeRequest
import com.server.dk.dto.toSavedEmployeeResponse
import com.server.dk.entities.Company
import com.server.dk.entities.Employee
import com.server.dk.entities.Payment
import com.server.common.entities.User
import com.server.dk.enums.*
import com.server.dk.model.SalarySlipForHTML
import com.server.common.properties.PdfProperties
import com.server.common.provider.UniqueIdProvider
import com.server.dk.service.PdfService
import com.server.common.utils.CloudUploadDownloadUtils
import com.server.common.utils.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import kotlin.math.ceil

@Component
class EmployeeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var paymentProvider: PaymentProvider

    @Autowired
    private lateinit var attendanceProvider: AttendanceProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var pdfService: PdfService

    @Autowired
    private lateinit var pdfProperties: PdfProperties

    @Autowired
    private lateinit var cloudUploadDownloadUtils: CloudUploadDownloadUtils

    fun getEmployee(employeeId: String): Employee? =
        try {
            employeeRepository.findById(employeeId).get()
        } catch (e: Exception) {
            null
        }

    fun getEmployees(company: Company, joinedBeforeDateTime: LocalDateTime): List<Employee> {
        return try {
            // We are adding one day to check for anyone who has been added as an employee today
            // and hence the attendance count has to consider that
            // There could be scenarios where we added the employees at 9:00 am on March 21, 2021
            // But the datetime form DateUtils.parseStandardDate(forDate) will be March 21, 2021, 00am
            // So the new employees will not be picked up
            employeeRepository.getEmployees(company.id, joinedBeforeDateTime.plusDays(1))
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveEmployee(createdByUser: User, createdForUser: User, company: Company, saveEmployeeRequest: SaveEmployeeRequest) : Employee {
        val newEmployee = Employee()
        newEmployee.id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.EMP.name)
        newEmployee.name = saveEmployeeRequest.name
        newEmployee.balanceInPaisaTillNow = 0
//        newEmployee.openingBalanceInPaisa = saveEmployeeRequest.openingBalanceInPaisa
        newEmployee.absoluteMobile = saveEmployeeRequest.absoluteMobile
        newEmployee.salaryAmountInPaisa = saveEmployeeRequest.salaryAmountInPaisa
        newEmployee.salaryType = saveEmployeeRequest.salaryType
        newEmployee.salaryCycle = saveEmployeeRequest.salaryCycle
//        newEmployee.openingBalanceType = saveEmployeeRequest.openingBalanceType ?: OpeningBalanceType.NONE
        newEmployee.joinedAt = if (saveEmployeeRequest.joinedAt != null) DateUtils.parseEpochInMilliseconds(saveEmployeeRequest.joinedAt) else DateUtils.dateTimeNow()
        newEmployee.company = company
        newEmployee.createdByUser = createdByUser
        newEmployee.createdForUser = createdForUser

        val savedEmployee = employeeRepository.save(newEmployee)

        if (saveEmployeeRequest.openingBalanceInPaisa != 0L && saveEmployeeRequest.openingBalanceType != OpeningBalanceType.NONE) {
            val paymentType = if (saveEmployeeRequest.openingBalanceType == OpeningBalanceType.ADVANCE) PaymentType.PAYMENT_OPENING_BALANCE_ADVANCE else PaymentType.PAYMENT_OPENING_BALANCE_PENDING
            paymentProvider.savePaymentAndDependentData(
                addedBy = createdByUser,
                company = company,
                employee = savedEmployee,
                forDate = DateUtils.toStringForDate(DateUtils.dateTimeNow()),
                paymentType = paymentType,
                amountInPaisa = saveEmployeeRequest.openingBalanceInPaisa,
                description = "Added by ${createdByUser.fullName} for opening balance"
            )
        }

        return savedEmployee
    }

    fun updateEmployee(payment: Payment) : Employee {
        val employee = payment.employee ?: error("Payment should always have an employee object")
        employee.balanceInPaisaTillNow = employee.balanceInPaisaTillNow + (payment.multiplierUsed * payment.amountInPaisa)
        return employeeRepository.save(employee)
    }

    fun findByCompany(company: Company): List<Employee>? {
        return employeeRepository.findByCompany(company)
    }

    fun removeEmployee(removeEmployeeRequest: RemoveEmployeeRequest): Employee {
        val employee = getEmployee(removeEmployeeRequest.employeeId)
        employee?.let {
            val toBeSavedEmployee = it
            toBeSavedEmployee.leftAt = DateUtils.dateTimeNow()
            toBeSavedEmployee.deleted = true
            return employeeRepository.save(toBeSavedEmployee)
        } ?: error("Unable to remove employee with employeeId: ${removeEmployeeRequest.employeeId}")
    }

    fun updateSalary(employee: Employee, forDate: String? = null) {

        /**
         *
         * Things that would change salary for any date:
         *
         * 1. Attendance: Present / Absent / Half Day
         * 2. Holiday: Paid / Non Paid
         *
         * So anywhere we mark these IN THE PAST, we need to update the salary as well
         *
         * Why only in the past?
         *
         * Because, in future, the daily job would take care of this.
         * But for past, we would have already calculated the salary hence we need to add this.
         *
         * */

        // Yesterday is used for cases when this method is called by the auto scheduled job
        val yesterday = DateUtils.dateTimeNow().minusDays(1)
        val dateToBeUsed = if (forDate == null) yesterday else DateUtils.parseStandardDate(forDate)
        val salaryAmountForDate = getSalaryAmountForDate(employee, DateUtils.toStringForDate(dateToBeUsed))
        paymentProvider.updateSalary(employee, salaryAmountForDate, DateUtils.toStringForDate(dateToBeUsed))
    }

    // This is only for 1 day
    private fun getSalaryAmountForDate(employee: Employee, forDate: String): Long {

        val company = employee.company ?: error("Employee should always have the company")

        val attendanceReportForEmployee: AttendanceReportForEmployee?
        val dateToBeUsed = DateUtils.parseStandardDate(forDate)
        val startDateTimeForYesterday = dateToBeUsed.toLocalDate().atStartOfDay()
        val endDateTimeForYesterday = dateToBeUsed.toLocalDate().atTime(LocalTime.MAX)
        attendanceReportForEmployee = attendanceProvider.getAttendanceReportForEmployee(
            employee = employee,
            startTime = startDateTimeForYesterday,
            endTime = endDateTimeForYesterday
        )

        if (attendanceReportForEmployee == null) {
            return 0L
        }

        logger.debug(attendanceReportForEmployee.toString())

        val fullPayDays = attendanceReportForEmployee.presentDays + attendanceReportForEmployee.paidHolidays
        val halfPayDays = attendanceReportForEmployee.halfDays

        val totalNumberOfDays = when (employee.salaryType) {
            SalaryType.WEEKLY -> 7
            SalaryType.MONTHLY,
            SalaryType.DAILY,
            SalaryType.PER_HOUR -> {
                if (company.salaryPaymentSchedule == SalaryPaymentSchedule.MONTHLY_30_DAYS) {
                    30
                } else {
                    YearMonth.of(dateToBeUsed.year, dateToBeUsed.monthValue).lengthOfMonth()
                }
            }
            SalaryType.ONE_TIME -> {
                logger.error("Salary can not be calculated for one time employees")
                0L
            }
        }

        val oneDaySalary = employee.salaryAmountInPaisa.toDouble() / totalNumberOfDays.toDouble()

        return when {
            fullPayDays > 0 -> {
                ceil(oneDaySalary).toLong()
            }
            halfPayDays > 0 -> {
                ceil(oneDaySalary/2).toLong()
            }
            else -> {
                0L
            }
        }
    }

    fun getEmployeeWorkingDetailsForMonthWithDate(employee: Employee, withDate: LocalDateTime): EmployeeWorkingDetailsForMonthWithDate {

        val monthStartDate = DateUtils.getStartDateForMonthWithDate(withDate)

        val startDateTime = if (monthStartDate.isAfter(employee.joinedAt)) {
            monthStartDate
        } else {
            employee.joinedAt
        }
        var endDateTime = DateUtils.getLastDateForMonthWithDate(withDate)
        val now = DateUtils.dateTimeNow()

        if (endDateTime.isAfter(now)) {
            endDateTime = now
        }

        if (employee.leftAt != null && now.isAfter(employee.leftAt)) {
            endDateTime = employee.leftAt!!
        }

        val workingDays = getEmployeeWorkingDays(employee, startDateTime, endDateTime)

        return EmployeeWorkingDetailsForMonthWithDate(
            employee = employee,
        withDate = withDate,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        workingDays = workingDays,
        )
    }

    private fun getEmployeeWorkingDays(employee: Employee, startDateTime: LocalDateTime, endDateTime: LocalDateTime): Int {

        val attendanceReportForEmployee = attendanceProvider.getAttendanceReportForEmployee(
            employee = employee,
            startTime = startDateTime,
            endTime = endDateTime
        )

        return attendanceReportForEmployee?.let {
            it.presentDays + it.halfDays
        } ?: -1
    }

    fun getEmployeeWorkingStartDateFromThisDate(fromDate: LocalDateTime): LocalDateTime {
        return DateUtils.dateTimeNow()
    }

    fun getEmployeeWorkingEndDateFromThisDate(fromDate: LocalDateTime): LocalDateTime {
        return DateUtils.dateTimeNow()
    }

    fun updateEmployeeJoiningDate(employee: Employee, newJoiningTime: Long): Employee {
        employee.joinedAt = DateUtils.parseEpochInMilliseconds(newJoiningTime)
        return employeeRepository.save(employee)
    }

    fun generatePdfForSalarySlip(employee: Employee, startDate: String, endDate: String): SalarySlipResponse? {
        val pdfFile = pdfService.generatePdfForData(
            templateName = pdfProperties.salarySlip.templateName,
            variableName = pdfProperties.salarySlip.variableName,
            dataForVariableName = getSalarySlipForHTML()
        )
        val company = employee.company ?: error("Company is missing")
        val publicUrl = cloudUploadDownloadUtils.uploadFile(
            pdfFile,
            "dukaankhata-user-uploads",
            "company/${company.id}/salarySlip/${employee.id}",
            "${employee.id}_salary_slip_${DateUtils.dateTimeNow().toString()}.pdf"
        )
        return SalarySlipResponse(
            employee = employee.toSavedEmployeeResponse(),
            startDate = startDate,
            endDate = endDate,
            salarySlipUrl = publicUrl
        )
    }
    private fun getSalarySlipForHTML(): SalarySlipForHTML {
        return SalarySlipForHTML(
            employeeName = "asd",
            paymentDetails = emptyList(),
            totalPaymentAmount = "sadas",
            totalDeductionsAmount = "asda",
            employerName = "asda",
            employerContact = "g",
        )
    }
}
