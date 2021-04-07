package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.EmployeeRepository
import com.dukaankhata.server.dto.AttendanceForEmployeeSalary
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
import com.dukaankhata.server.service.SchedulerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import kotlin.math.ceil
import kotlin.math.max

@Component
class EmployeeUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var paymentUtils: PaymentUtils

    @Autowired
    private lateinit var attendanceUtils: AttendanceUtils

    @Autowired
    private lateinit var schedulerService: SchedulerService

    fun getEmployee(employeeId: Long): Employee? =
        try {
            employeeRepository.findById(employeeId).get()
        } catch (e: Exception) {
            null
        }

    suspend fun getEmployeesForDate(companyId: Long, forDate: String): List<Employee> =
        try {
            // We are adding one day to check for anyone who has been added as an employee today
            // and hence the attendance count has to consider that
            // There could be scenarios where we added the employees at 9:00 am on March 21, 2021
            // But the datetime form DateUtils.parseStandardDate(forDate) will be March 21, 2021, 00am
            // So the new employees will not be picked up
            employeeRepository.getEmployeesForDate(companyId, DateUtils.parseStandardDate(forDate).plusDays(1))
        } catch (e: Exception) {
            emptyList()
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

    fun updateSalary(employee: Employee) {

        val attendanceForEmployeeSalary: AttendanceForEmployeeSalary?

        when (employee.salaryType) {
            // Paid Monthly
            SalaryType.MONTHLY, SalaryType.DAILY, SalaryType.PER_HOUR -> {
                logger.info("Update salary on the monthly basis")

                // This is used only to set the schedule
                val salaryCycleMonthDay = employee.salaryCycle.split("_")[1].toInt()

                // Get attendance for last month
                // Choosing a random date in middle to select correct start and end month
                val someDateInLastMonth = DateUtils.dateTimeNow().minusMonths(1)
                var startDateTimeOfThePrevMonth = someDateInLastMonth.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay()

                if (employee.joinedAt.isAfter(startDateTimeOfThePrevMonth)) {
                    // First time Salary calculation for an employee
                    startDateTimeOfThePrevMonth = employee.joinedAt
                }
                val endDateTimeOfThePrevMonth = someDateInLastMonth.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX)

                if (startDateTimeOfThePrevMonth.isAfter(endDateTimeOfThePrevMonth)) {
                    error("Employee does not need to have his salary updated. He joined this month only")
                }

                attendanceForEmployeeSalary = attendanceUtils.getAttendanceForSalary(
                    employee = employee,
                    startTime = startDateTimeOfThePrevMonth,
                    endTime = endDateTimeOfThePrevMonth
                )
                logger.debug(attendanceForEmployeeSalary.toString())
            }

            // Paid weekly
            SalaryType.WEEKLY -> {
                logger.info("Update salary on the weekly basis")
                val startWeekDay = DayOfWeek.valueOf(employee.salaryCycle.split("_")[1])
                // Get attendance for last week
                val endWeekDay = when (startWeekDay) {
                    DayOfWeek.MONDAY -> DayOfWeek.SUNDAY
                    DayOfWeek.TUESDAY -> DayOfWeek.MONDAY
                    DayOfWeek.WEDNESDAY -> DayOfWeek.TUESDAY
                    DayOfWeek.THURSDAY -> DayOfWeek.WEDNESDAY
                    DayOfWeek.FRIDAY -> DayOfWeek.THURSDAY
                    DayOfWeek.SATURDAY -> DayOfWeek.FRIDAY
                    DayOfWeek.SUNDAY -> DayOfWeek.SATURDAY
                }

                val now = DateUtils.dateTimeNow()

                val endDateTimeOfPreviousWeek = now.with(TemporalAdjusters.previous(endWeekDay))
                val startDateTimeOfPreviousWeek = endDateTimeOfPreviousWeek.with(TemporalAdjusters.previous(startWeekDay))
                attendanceForEmployeeSalary = attendanceUtils.getAttendanceForSalary(
                    employee = employee,
                    startTime = startDateTimeOfPreviousWeek,
                    endTime = endDateTimeOfPreviousWeek
                )
                logger.debug(attendanceForEmployeeSalary.toString())
            }

            // No Schedule
            SalaryType.ONE_TIME -> {
                logger.info("There should be NO schedule for this case. So in case you find one, un-schedule that job")
                logger.error("===INVALID CASE===")
                schedulerService.unScheduleEmployeeSalaryUpdate(employee)
                return
            }
        }

        val company = employee.company ?: error("Employee should always have the company")

        val fullPayDays = attendanceForEmployeeSalary.presentDays + attendanceForEmployeeSalary.paidHolidays
        val halfPayDays = attendanceForEmployeeSalary.halfDaysDays
        val noPayDays = attendanceForEmployeeSalary.absentDays + attendanceForEmployeeSalary.nonPaidHolidays

        var totalNumberOfDays = attendanceForEmployeeSalary.totalDay
        if (employee.salaryType != SalaryType.WEEKLY && company.salaryPaymentSchedule == SalaryPaymentSchedule.MONTHLY_30_DAYS) {
            // There might be 31 or 28 or 29
            // But if the Employer has chosen to keep the schedule to be 30 days every month
            // then we apply that to all employees that get paid monthly
            // For weekly, we always keep 7
            totalNumberOfDays = 30
        }

        val dailyWage = employee.salaryAmountInPaisa / totalNumberOfDays

        val totalSalary = ceil ((fullPayDays * dailyWage) + ceil(((halfPayDays.toDouble() * dailyWage.toDouble())/2)))

        // or

        val totalSalaryV2 = employee.salaryAmountInPaisa - ((noPayDays * dailyWage) + ceil(((halfPayDays.toDouble() * dailyWage.toDouble())/2)))

        // Overtime and Late fines are deducted at the time of adding so no need to add or deduct from salary
        // Choosing max to account for any missed values while doing division
        val amountToBeCredited = ceil(max(totalSalary, totalSalaryV2)).toLong()
        logger.debug("amountToBeCredited: $amountToBeCredited")

        // TODO: Add a check at all the places where we update attendance or anything
        // related to Salary that if the salary is already calculated then
        // NO ONE can make the changes in the past data.
        // They are free make changes to past data as long as salary is not calculated
        // For that time period
        paymentUtils.savePaymentAndDependentData(
            addedBy = company.user!!,
            company = company,
            employee = employee,
            forDate = DateUtils.toStringDate(DateUtils.dateTimeNow()),
            paymentType = PaymentType.PAYMENT_SALARY,
            amountInPaisa = amountToBeCredited,
            description = "Added by system for Salary from ${attendanceForEmployeeSalary.startDate} to ${attendanceForEmployeeSalary.endDate}"
        )
    }
}
