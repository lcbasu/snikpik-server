package com.dukaankhata.server.utils

import AttendanceReportForEmployee
import com.dukaankhata.server.dao.EmployeeRepository
import com.dukaankhata.server.dto.RemoveEmployeeRequest
import com.dukaankhata.server.dto.SaveEmployeeRequest
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.OpeningBalanceType
import com.dukaankhata.server.enums.PaymentType
import com.dukaankhata.server.enums.SalaryPaymentSchedule
import com.dukaankhata.server.enums.SalaryType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import kotlin.math.ceil

@Component
class EmployeeUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var paymentUtils: PaymentUtils

    @Autowired
    private lateinit var attendanceUtils: AttendanceUtils

    fun getEmployee(employeeId: Long): Employee? =
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
        newEmployee.name = saveEmployeeRequest.name
        newEmployee.balanceInPaisaTillNow = 0
//        newEmployee.openingBalanceInPaisa = saveEmployeeRequest.openingBalanceInPaisa
        newEmployee.phoneNumber = saveEmployeeRequest.phoneNumber
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
            paymentUtils.savePaymentAndDependentData(
                addedBy = createdByUser,
                company = company,
                employee = savedEmployee,
                forDate = DateUtils.toStringDate(DateUtils.dateTimeNow()),
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
        val salaryAmountForDate = getSalaryAmountForDate(employee, DateUtils.toStringDate(dateToBeUsed))
        paymentUtils.updateSalary(employee, salaryAmountForDate, DateUtils.toStringDate(dateToBeUsed))
    }

    // This is only for 1 day
    private fun getSalaryAmountForDate(employee: Employee, forDate: String): Long {

        val company = employee.company ?: error("Employee should always have the company")

        val attendanceReportForEmployee: AttendanceReportForEmployee?
        val dateToBeUsed = DateUtils.parseStandardDate(forDate)
        val startDateTimeForYesterday = dateToBeUsed.toLocalDate().atStartOfDay()
        val endDateTimeForYesterday = dateToBeUsed.toLocalDate().atTime(LocalTime.MAX)
        attendanceReportForEmployee = attendanceUtils.getAttendanceReportForEmployee(
            employee = employee,
            startTime = startDateTimeForYesterday,
            endTime = endDateTimeForYesterday
        )
        logger.debug(attendanceReportForEmployee.toString())

        val fullPayDays = attendanceReportForEmployee.presentDays + attendanceReportForEmployee.paidHolidays
        val halfPayDays = attendanceReportForEmployee.halfDaysDays

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
            SalaryType.ONE_TIME -> error("Salary can not be calculated for one time employees")
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
}
