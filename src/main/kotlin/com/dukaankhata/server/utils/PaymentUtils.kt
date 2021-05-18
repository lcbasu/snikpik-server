package com.dukaankhata.server.utils

import DailyPayment
import MonthPayment
import SalaryReversal
import com.dukaankhata.server.dao.PaymentRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.MonthlyPaymentType
import com.dukaankhata.server.enums.PaymentType
import com.dukaankhata.server.enums.ReadableIdPrefix
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PaymentUtils {

    private val GO_BACK_MONTHS = 2L

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    fun getPayment(paymentId: String): Payment? =
        try {
            paymentRepository.findById(paymentId).get()
        } catch (e: Exception) {
            null
        }

    fun getPaymentsForDate(employee: Employee, forDate: String): List<Payment> =
        try {
            paymentRepository.getPaymentsForDate(employee.id, forDate)
        } catch (e: Exception) {
            emptyList()
        }

    fun savePaymentAndDependentData(addedBy: User, company: Company, employee: Employee, forDate: String, paymentType: PaymentType, amountInPaisa: Long, description: String?) : Payment {
        val payment = savePaymentAtomic(
            addedBy = addedBy,
            company = company,
            employee = employee,
            forDate = forDate,
            paymentType = paymentType,
            amountInPaisa = amountInPaisa,
            description = description
        )

        // Update the employee payment
        employeeUtils.updateEmployee(payment)

        // Update the company payment
        companyUtils.updateCompany(payment)

        return updatePaymentWithAuditDetails(payment)
    }

    fun updatePaymentWithAuditDetails(payment: Payment): Payment {
        payment.companyNewAmountInPaisa = payment.company!!.totalDueAmountInPaisa
        payment.employeeNewAmountInPaisa = payment.employee!!.balanceInPaisaTillNow
        return paymentRepository.save(payment)
    }

    fun savePaymentAtomic(addedBy: User, company: Company, employee: Employee, forDate: String, paymentType: PaymentType, amountInPaisa: Long, description: String?) : Payment {
        /**
         *
         * +1 -> EMPLOYEE owes to EMPLOYER. This will REDUCE the amount of money owed by employer to employee
         * -1 -> EMPLOYER owes to EMPLOYEE. This will INCREASE the amount of money owed by employer to employee
         *
         * */
        val multiplierUsed = when (paymentType) {
            PaymentType.PAYMENT_ONE_TIME_PAID -> 1
            PaymentType.PAYMENT_ONE_TIME_TOOK -> -1
            PaymentType.PAYMENT_ALLOWANCE -> -1
            PaymentType.PAYMENT_BONUS -> -1
            PaymentType.PAYMENT_DEDUCTIONS -> 1
            PaymentType.PAYMENT_LOAN -> 1 // +1 as this is kind of advance given to employee by employer
            PaymentType.PAYMENT_ATTENDANCE_LATE_FINE -> 1
            PaymentType.PAYMENT_ATTENDANCE_OVERTIME -> -1
            PaymentType.PAYMENT_OPENING_BALANCE_ADVANCE -> 1
            PaymentType.PAYMENT_OPENING_BALANCE_PENDING -> -1
            PaymentType.PAYMENT_SALARY -> -1
            PaymentType.PAYMENT_SALARY_REVERSAL -> 1 // Added back
            PaymentType.NONE -> 0
        }

        val payment = Payment()
        payment.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.PMT.name)
        payment.company = company
        payment.employee = employee
        payment.paymentType = paymentType
        payment.addedBy = addedBy
        payment.amountInPaisa = amountInPaisa
        payment.description = description
        payment.forDate = forDate
        payment.multiplierUsed = multiplierUsed
        payment.addedAt = DateUtils.dateTimeNow()
        payment.companyOldAmountInPaisa = company.totalDueAmountInPaisa
        payment.employeeOldAmountInPaisa = employee.balanceInPaisaTillNow
        return paymentRepository.save(payment)
    }

    fun getAllPaymentsBetweenGivenTimes(companyId: String, startTime: LocalDateTime, endTime: LocalDateTime): List<Payment> =
        try {
            paymentRepository.getAllPaymentsBetweenGivenTimes(companyId, startTime, endTime)
        } catch (e: Exception) {
            emptyList()
        }

    fun getPayments(companyId: String, datesList: List<String>): List<Payment> =
        try {
            paymentRepository.getPayments(companyId, datesList)
        } catch (e: Exception) {
            emptyList()
        }

    fun getPaymentsForEmployee(employeeId: String, datesList: List<String>): List<Payment> =
        try {
            paymentRepository.getPaymentsForEmployee(employeeId, datesList)
        } catch (e: Exception) {
            emptyList()
        }

    fun updateSalary(employee: Employee, salaryAmountForDate: Long, forDate: String) {

        // Get the last salary payment for that day. We will need to revert that
        val lastSalaryPaymentForThatDay = getPaymentsForDate(employee, forDate)
            .filter { it.paymentType == PaymentType.PAYMENT_SALARY }
            .maxByOrNull { DateUtils.getEpoch(it.lastModifiedAt) }

        val company = employee.company ?: error("Employee should always have the company")
        if (lastSalaryPaymentForThatDay == null) {
            // This is the fist salary payment so go ahead as is
            // Add the new Salary Payment
            val newPayment = savePaymentAndDependentData(
                addedBy = company.user!!,
                company = company,
                employee = employee,
                forDate = forDate,
                paymentType = PaymentType.PAYMENT_SALARY,
                amountInPaisa = salaryAmountForDate,
                description = "Added by system for adding the salary"
            )
        } else {
            // 1. Revert the last salary payment
            // 2. Add the new Salary Payment
            reCalculateSalary(
                addedBy = company.user!!,
                company = company,
                employee = employee,
                forDate = forDate,
                lastSalaryPaymentForThatDay = lastSalaryPaymentForThatDay,
                newSalaryAmount = salaryAmountForDate
            )
        }
    }

    @Transactional
    fun reCalculateSalary(
        lastSalaryPaymentForThatDay: Payment,
        newSalaryAmount: Long,
        forDate: String,
        addedBy: User,
        company: Company,
        employee: Employee
    ): SalaryReversal {

        val revertOldPayment = savePaymentAndDependentData(
            addedBy = addedBy,
            company = company,
            employee = employee,
            forDate = forDate,
            paymentType = PaymentType.PAYMENT_SALARY_REVERSAL,
            amountInPaisa = lastSalaryPaymentForThatDay.amountInPaisa,
            description = "Added by system for reverting the salary while Re-calculating the salary"
        )

        val newPayment = savePaymentAndDependentData(
            addedBy = addedBy,
            company = company,
            employee = employee,
            forDate = forDate,
            paymentType = PaymentType.PAYMENT_SALARY,
            amountInPaisa = newSalaryAmount,
            description = "Added by system for adding the salary while Re-calculating the salary"
        )

        return SalaryReversal(
            revertedSalaryPayment = revertOldPayment,
            newSalaryPayment = newPayment
        )
    }

    fun getMonthlyPaymentSummary(company: Company, forYear: Int, forMonth: Int): List<MonthPayment> {
        val reportDuration = DateUtils.getReportDuration(forYear, forMonth)
        val startTime = reportDuration.startTime.minusMonths(GO_BACK_MONTHS)
        val datesList = DateUtils.getDatesBetweenInclusiveOfStartAndEndDates(startTime, reportDuration.endTime).map { DateUtils.toStringDate(it) }
        val payments = getPayments(companyId = company.id, datesList = datesList)
        return runBlocking {
            val employees = employeeUtils.getEmployees(company, reportDuration.endTime)
            val monthPayments = mutableListOf<MonthPayment>()

            employees.map { employee ->
                async {
                    getMonthlyPaymentSummary(listOf(employee), payments, datesList)
                }
            }.map {
                val result = it.await()
                monthPayments.addAll(result)
            }
            monthPayments
        }
    }

    fun getMonthlyPaymentSummary(employees: List<Employee>, payments: List<Payment>, datesList: List<String>): List<MonthPayment> {
        val groupedPayments = payments.groupBy { it.forDate }
        val dailyPayments = mutableListOf<DailyPayment>()
        employees.map { employee ->
            datesList.map { forDate ->
                var paymentsAmount = 0L
                var salaryAmount = 0L
                val employeePaymentsForDate = groupedPayments.getOrDefault(forDate, emptyList()).filter { it.employee?.id == employee.id }
                if (employeePaymentsForDate.isEmpty()) {
                    dailyPayments.add(
                        DailyPayment(
                            employee = employee,
                            forDate = forDate,
                            paymentsAmount = paymentsAmount,
                            salaryAmount = salaryAmount
                        )
                    )
                } else {
                    // If not empty then calculate based on the actual values
                    employeePaymentsForDate.groupBy { it.paymentType }.map { employeePaymentsForEachType ->
                        val paymentType = employeePaymentsForEachType.key
                        val allPayments = employeePaymentsForEachType.value

                        if (paymentType == PaymentType.NONE || paymentType == PaymentType.PAYMENT_SALARY_REVERSAL) {
                            // Do nothing
                            allPayments
                        } else if (paymentType == PaymentType.PAYMENT_SALARY) {
                            // Get the last entry for that day and use it. DO NOT use all the entries as all the previous ones for that day have been REVERSED
                            val lastSalaryPaymentForThatDay = allPayments.maxByOrNull { DateUtils.getEpoch(it.lastModifiedAt) }
                            salaryAmount += lastSalaryPaymentForThatDay?.let { it.amountInPaisa * it.multiplierUsed } ?: 0
                        } else {
                            // Add all remaining as the payment for that day
                            allPayments.map {
                                paymentsAmount += (it.amountInPaisa * it.multiplierUsed)
                            }
                        }
                    }
                    dailyPayments.add(
                        DailyPayment(
                            employee = employee,
                            forDate = forDate,
                            paymentsAmount = paymentsAmount,
                            salaryAmount = salaryAmount
                        )
                    )
                }
            }
        }

        val monthPayments = mutableListOf<MonthPayment>()

        dailyPayments.groupBy { it.employee }.map { employeeDailyPayments ->
            employeeDailyPayments.value.groupBy { DateUtils.parseStandardDate(it.forDate).monthValue }.map { employeePaymentsForMonth ->
                var salaryAmountForMonth = 0L
                var paymentAmountForMonth = 0L
                employeePaymentsForMonth.value.map {
                    paymentAmountForMonth += it.paymentsAmount
                    salaryAmountForMonth += it.salaryAmount
                }
                monthPayments.add(
                    MonthPayment(
                        employee = employeeDailyPayments.key,
                        monthNumber = employeePaymentsForMonth.key,
                        actualSalary = employeeDailyPayments.key.salaryAmountInPaisa,
                        paymentsAmount = paymentAmountForMonth,
                        salaryAmount = salaryAmountForMonth,
                        closingBalance = paymentAmountForMonth + salaryAmountForMonth
                    )
                )
            }
        }
        return monthPayments
    }

    fun getMonthlyPaymentSummary(employee: Employee, forYear: Int, forMonth: Int): List<MonthPayment> {
        val reportDuration = DateUtils.getReportDuration(forYear, forMonth)
        val startTime = reportDuration.startTime.minusMonths(GO_BACK_MONTHS)
        val datesList = DateUtils.getDatesBetweenInclusiveOfStartAndEndDates(startTime, reportDuration.endTime).map { DateUtils.toStringDate(it) }
        val payments = getPaymentsForEmployee(employeeId = employee.id, datesList = datesList)
        return getMonthlyPaymentSummary(listOf(employee), payments, datesList)
    }

    fun getCompanyPaymentReport(forYear: Int, forMonth: Int, company: Company, monthlyPayments: List<MonthPayment>): CompanyPaymentReportResponse {
        val companyResponse = company.toSavedCompanyResponse()
        val randomDateInMonth = DateUtils.getRandomDateInMonth(forYear = forYear, forMonth = forMonth)
        val prevMonthNumber = randomDateInMonth.minusMonths(1).monthValue

        val employeePaymentsResponse = monthlyPayments.groupBy { it.employee }.map { forEmployee ->
            val employeeMonthlyPayments = getMonthlyPaymentsResponse(forEmployee.value)
            EmployeePaymentReportResponse(
                employee = forEmployee.key.toSavedEmployeeResponse(),
                currentMonthNumber = forMonth,
                currentMonthSalary = getTotalAMount(employeeMonthlyPayments, forMonth, MonthlyPaymentType.SALARY),
                currentMonthPayments = getTotalAMount(employeeMonthlyPayments, forMonth, MonthlyPaymentType.PAYMENT),
                prevMonthNumber = prevMonthNumber,
                prevMonthClosing = getTotalAMount(employeeMonthlyPayments, prevMonthNumber, MonthlyPaymentType.CLOSING),
                monthlyPayments = employeeMonthlyPayments,
            )
        }

        val companyMonthlyPayments = getMonthlyPaymentsResponse(monthlyPayments)
        return CompanyPaymentReportResponse(
            company = companyResponse,
            currentMonthNumber = forMonth,
            currentMonthSalary = getTotalAMount(companyMonthlyPayments, forMonth, MonthlyPaymentType.SALARY),
            currentMonthPayments = getTotalAMount(companyMonthlyPayments, forMonth, MonthlyPaymentType.PAYMENT),
            prevMonthNumber = prevMonthNumber,
            prevMonthClosing = getTotalAMount(companyMonthlyPayments, prevMonthNumber, MonthlyPaymentType.CLOSING),
            monthlyPayments = companyMonthlyPayments,
            employeePayments = employeePaymentsResponse
        )
    }

    fun getTotalAMount(monthlyPayments: List<MonthPaymentResponse>, forMonth: Int, monthlyPaymentType: MonthlyPaymentType): Long {
        var sum = 0L
        monthlyPayments.filter { it.monthNumber == forMonth && it.monthlyPaymentType == monthlyPaymentType }.map {
            sum += it.amount
        }
        return sum
    }

    private fun getMonthlyPaymentsResponse(monthlyPayments: List<MonthPayment>): List<MonthPaymentResponse> {

        val monthPaymentResponse = mutableListOf<MonthPaymentResponse>()

        monthlyPayments.groupBy { it.monthNumber }.map { forMonth ->
            var salary = 0L
            var closing = 0L
            var payments = 0L

            forMonth.value.map {
                salary += it.salaryAmount
                payments += it.paymentsAmount
                closing += it.closingBalance
            }
            monthPaymentResponse.add(MonthPaymentResponse(
                monthNumber = forMonth.key,
                amount = salary,
                monthlyPaymentType = MonthlyPaymentType.SALARY
            ))
            monthPaymentResponse.add(MonthPaymentResponse(
                monthNumber = forMonth.key,
                amount = payments,
                monthlyPaymentType = MonthlyPaymentType.PAYMENT
            ))
            monthPaymentResponse.add(MonthPaymentResponse(
                monthNumber = forMonth.key,
                amount = closing,
                monthlyPaymentType = MonthlyPaymentType.CLOSING
            ))
        }

        return monthPaymentResponse
    }

    fun getEmployeePaymentDetails(employee: Employee, forYear: Int, forMonth: Int, monthlyPaymentSummary: List<MonthPayment>): EmployeePaymentDetailsResponse? {
        val monthlyPaymentsResponse = getMonthlyPaymentsResponse(monthlyPaymentSummary)
        val employeeWorkingDetailsForMonthWithDate = employeeUtils.getEmployeeWorkingDetailsForMonthWithDate(employee, DateUtils.dateTimeNow())
        return EmployeePaymentDetailsResponse(
            employee = employee.toSavedEmployeeResponse(),
            currentMonthNumber = forMonth,
            currentMonthWorkingStartDate = DateUtils.getEpoch(employeeWorkingDetailsForMonthWithDate.startDateTime),
            currentMonthWorkingEndDate = DateUtils.getEpoch(employeeWorkingDetailsForMonthWithDate.endDateTime),
            currentMonthWorkingDays = employeeWorkingDetailsForMonthWithDate.workingDays,
            currentMonthActualSalary = employee.salaryAmountInPaisa,
            currentMonthPaidSalary = getTotalAMount(monthlyPaymentsResponse, forMonth, MonthlyPaymentType.SALARY),
            currentMonthPayments = getTotalAMount(monthlyPaymentsResponse, forMonth, MonthlyPaymentType.PAYMENT),
            monthlyPayments = monthlyPaymentsResponse
        )
    }
}
