package com.server.dk.provider

import DailyPayment
import MonthPayment
import SalaryReversal
import com.server.dk.dao.PaymentRepository
import com.server.dk.dto.*
import com.server.dk.entities.Company
import com.server.dk.entities.Employee
import com.server.dk.entities.Payment
import com.server.dk.entities.User
import com.server.dk.enums.MonthlyPaymentType
import com.server.dk.enums.PaymentType
import com.server.dk.enums.ReadableIdPrefix
import com.server.dk.utils.CommonUtils
import com.server.dk.utils.DateUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PaymentProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val GO_BACK_MONTHS = 2L

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var employeeProvider: EmployeeProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

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

        logger.info("Payment saved for ${employee.id} for $forDate with amountInPaisa: $amountInPaisa and type: $paymentType. description: $description, addedBy: $addedBy")

        // Update the employee payment
        employeeProvider.updateEmployee(payment)

        // Update the company payment
        companyProvider.updateCompany(payment)

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
        payment.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PMT.name)
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
            logger.info("Salary ADDED for employeeId: ${employee.id} with newSalaryAmount: $salaryAmountForDate for $forDate")
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
            logger.info("Salary RE-CALCULATED for employeeId: ${employee.id} with newSalaryAmount: $salaryAmountForDate and lastSalaryPaymentForThatDay: $lastSalaryPaymentForThatDay for $forDate")
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
            val employees = employeeProvider.getEmployees(company, reportDuration.endTime)
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

    fun getDailyPayments(employee: Employee, payments: List<Payment>, datesList: List<String>): List<DailyPayment> {
        val groupedPayments = payments.groupBy { it.forDate }
        val dailyPayments = mutableListOf<DailyPayment>()
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
                        salaryAmount = salaryAmount,
                        payments = emptyList()
                    )
                )
            } else {
                // If not empty then calculate based on the actual values
                val allPaymentsForThatDay = mutableListOf<Payment>()
                employeePaymentsForDate.groupBy { it.paymentType }.map { employeePaymentsForEachType ->
                    val paymentType = employeePaymentsForEachType.key
                    val allPayments = employeePaymentsForEachType.value

                    if (paymentType == PaymentType.NONE || paymentType == PaymentType.PAYMENT_SALARY_REVERSAL) {
                        // Do nothing
                        allPayments
                    } else if (paymentType == PaymentType.PAYMENT_SALARY) {
                        // Get the last entry for that day and use it. DO NOT use all the entries as all the previous ones for that day have been REVERSED
                        val lastSalaryPaymentForThatDay = allPayments.maxByOrNull { DateUtils.getEpoch(it.lastModifiedAt) }
                        lastSalaryPaymentForThatDay?.let {
                            allPaymentsForThatDay.add(it)
                        }
                        salaryAmount += lastSalaryPaymentForThatDay?.let { it.amountInPaisa * it.multiplierUsed } ?: 0
                    } else {
                        // Add all remaining as the payment for that day
                        allPayments.map {
                            paymentsAmount += (it.amountInPaisa * it.multiplierUsed)
                        }
                        allPaymentsForThatDay.addAll(allPayments)
                    }
                }
                dailyPayments.add(
                    DailyPayment(
                        employee = employee,
                        forDate = forDate,
                        paymentsAmount = paymentsAmount,
                        salaryAmount = salaryAmount,
                        payments = allPaymentsForThatDay
                    )
                )
            }
        }
        return dailyPayments
    }

    fun getMonthlyPaymentSummary(employees: List<Employee>, payments: List<Payment>, datesList: List<String>): List<MonthPayment> {
        val dailyPayments = mutableListOf<DailyPayment>()
        employees.map { employee ->
            dailyPayments.addAll(getDailyPayments(employee, payments, datesList))
        }

        val monthPayments = mutableListOf<MonthPayment>()

        dailyPayments.groupBy { it.employee }.map { employeeDailyPayments ->
            employeeDailyPayments.value.groupBy { "${DateUtils.parseStandardDate(it.forDate).monthValue}${CommonUtils.STRING_SEPARATOR}${DateUtils.parseStandardDate(it.forDate).year}" }.map { employeePaymentsForMonth ->
                val (monthNumber, yearNumber) = employeePaymentsForMonth.key.split(CommonUtils.STRING_SEPARATOR)
                var salaryAmountForMonth = 0L
                var paymentAmountForMonth = 0L
                employeePaymentsForMonth.value.map {
                    paymentAmountForMonth += it.paymentsAmount
                    salaryAmountForMonth += it.salaryAmount
                }
                monthPayments.add(
                    MonthPayment(
                        employee = employeeDailyPayments.key,
                        yearNumber = yearNumber.toInt(),
                        monthNumber = monthNumber.toInt(),
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
        val prevMonthMonthNumber = randomDateInMonth.minusMonths(1).monthValue
        val prevMonthYearNumber = randomDateInMonth.minusMonths(1).year

        val employeePaymentsResponse = monthlyPayments.groupBy { it.employee }.map { forEmployee ->
            val employeeMonthlyPayments = getMonthlyPaymentsResponse(forEmployee.value)
            EmployeePaymentReportResponse(
                employee = forEmployee.key.toSavedEmployeeResponse(),
                currentYearNumber = forYear,
                currentMonthNumber = forMonth,
                currentMonthSalary = getTotalAMount(employeeMonthlyPayments, forMonth, forYear, MonthlyPaymentType.SALARY),
                currentMonthPayments = getTotalAMount(employeeMonthlyPayments, forMonth, forYear, MonthlyPaymentType.PAYMENT),
                prevMonthMonthNumber = prevMonthMonthNumber,
                prevMonthYearNumber = prevMonthYearNumber,
                prevMonthClosing = getTotalAMount(employeeMonthlyPayments, prevMonthMonthNumber, prevMonthYearNumber, MonthlyPaymentType.CLOSING),
                monthlyPayments = employeeMonthlyPayments,
            )
        }

        val companyMonthlyPayments = getMonthlyPaymentsResponse(monthlyPayments)
        return CompanyPaymentReportResponse(
            company = companyResponse,
            currentYearNumber = forYear,
            currentMonthNumber = forMonth,
            currentMonthSalary = getTotalAMount(companyMonthlyPayments, forMonth, forYear, MonthlyPaymentType.SALARY),
            currentMonthPayments = getTotalAMount(companyMonthlyPayments, forMonth, forYear, MonthlyPaymentType.PAYMENT),
            prevMonthMonthNumber = prevMonthMonthNumber,
            prevMonthYearNumber = prevMonthYearNumber,
            prevMonthClosing = getTotalAMount(companyMonthlyPayments, prevMonthMonthNumber, prevMonthYearNumber, MonthlyPaymentType.CLOSING),
            monthlyPayments = companyMonthlyPayments,
            employeePayments = employeePaymentsResponse
        )
    }

    fun getTotalAMount(monthlyPayments: List<MonthPaymentResponse>, forMonth: Int, forYear: Int, monthlyPaymentType: MonthlyPaymentType): Long {
        var sum = 0L
        monthlyPayments.filter { it.monthNumber == forMonth && it.yearNumber == forYear && it.monthlyPaymentType == monthlyPaymentType }.map {
            sum += it.amount
        }
        return sum
    }

    private fun getMonthlyPaymentsResponse(monthlyPayments: List<MonthPayment>): List<MonthPaymentResponse> {

        val monthPaymentResponse = mutableListOf<MonthPaymentResponse>()

        monthlyPayments.groupBy { "${it.monthNumber}${CommonUtils.STRING_SEPARATOR}${it.yearNumber}" }.map { forMonthAndYear ->
            var salary = 0L
            var closing = 0L
            var payments = 0L

            forMonthAndYear.value.map {
                salary += it.salaryAmount
                payments += it.paymentsAmount
                closing += it.closingBalance
            }

            val (monthNumber, yearNumber) = forMonthAndYear.key.split(CommonUtils.STRING_SEPARATOR)
            monthPaymentResponse.add(MonthPaymentResponse(
                yearNumber = yearNumber.toInt(),
                monthNumber = monthNumber.toInt(),
                amount = salary,
                monthlyPaymentType = MonthlyPaymentType.SALARY
            ))
            monthPaymentResponse.add(MonthPaymentResponse(
                yearNumber = yearNumber.toInt(),
                monthNumber = monthNumber.toInt(),
                amount = payments,
                monthlyPaymentType = MonthlyPaymentType.PAYMENT
            ))
            monthPaymentResponse.add(MonthPaymentResponse(
                yearNumber = yearNumber.toInt(),
                monthNumber = monthNumber.toInt(),
                amount = closing,
                monthlyPaymentType = MonthlyPaymentType.CLOSING
            ))
        }

        return monthPaymentResponse
    }

    fun getEmployeePaymentDetails(employee: Employee, forYear: Int, forMonth: Int, monthlyPaymentSummary: List<MonthPayment>): EmployeePaymentDetailsResponse? {
        val monthlyPaymentsResponse = getMonthlyPaymentsResponse(monthlyPaymentSummary)
        val employeeWorkingDetailsForMonthWithDate = employeeProvider.getEmployeeWorkingDetailsForMonthWithDate(employee, DateUtils.dateTimeNow())
        return EmployeePaymentDetailsResponse(
            employee = employee.toSavedEmployeeResponse(),
            currentYearNumber = forYear,
            currentMonthNumber = forMonth,
            currentMonthWorkingStartDate = DateUtils.getEpoch(employeeWorkingDetailsForMonthWithDate.startDateTime),
            currentMonthWorkingEndDate = DateUtils.getEpoch(employeeWorkingDetailsForMonthWithDate.endDateTime),
            currentMonthWorkingDays = employeeWorkingDetailsForMonthWithDate.workingDays,
            currentMonthActualSalary = employee.salaryAmountInPaisa,
            currentMonthPaidSalary = getTotalAMount(monthlyPaymentsResponse, forMonth, forYear, MonthlyPaymentType.SALARY),
            currentMonthPayments = getTotalAMount(monthlyPaymentsResponse, forMonth, forYear, MonthlyPaymentType.PAYMENT),
            monthlyPayments = monthlyPaymentsResponse
        )
    }

    fun getEmployeeCompletePaymentDetails(employee: Employee, forYear: Int, forMonth: Int): EmployeeCompletePaymentDetailsResponse? {
        val randomDateInMonth = DateUtils.getRandomDateInMonth(forYear = forYear, forMonth = forMonth)
        val employeeWorkingDetailsForMonthWithDate = employeeProvider.getEmployeeWorkingDetailsForMonthWithDate(employee, randomDateInMonth)
        val datesList = DateUtils.getDatesBetweenInclusiveOfStartAndEndDates(employeeWorkingDetailsForMonthWithDate.startDateTime, employeeWorkingDetailsForMonthWithDate.endDateTime).map { DateUtils.toStringDate(it) }
        val payments = getPaymentsForEmployee(employeeId = employee.id, datesList = datesList)
        val monthlyPayments = getMonthlyPaymentSummary(listOf(employee), payments, datesList)
        val dailyPayments = getDailyPayments(employee, payments, datesList)
        val monthlyPaymentsResponse = getMonthlyPaymentsResponse(monthlyPayments)

        val randomDateInLastMonth = randomDateInMonth.minusMonths(1)
        val prevMonthDatesList = DateUtils.getDatesBetweenInclusiveOfStartAndEndDates(DateUtils.getStartDateForMonthWithDate(randomDateInLastMonth), DateUtils.getLastDateForMonthWithDate(randomDateInLastMonth)).map { DateUtils.toStringDate(it) }
        val prevMonthPayments = getPaymentsForEmployee(employeeId = employee.id, datesList = prevMonthDatesList)
        val prevMonthMonthlyPayments = getMonthlyPaymentSummary(listOf(employee), prevMonthPayments, datesList)

        val prevMonthMonthlyPaymentsResponse = getMonthlyPaymentsResponse(prevMonthMonthlyPayments)
        val prevMonthMonthNumber = randomDateInLastMonth.monthValue
        val prevMonthYearNumber = randomDateInLastMonth.year

        return EmployeeCompletePaymentDetailsResponse(
            employee = employee.toSavedEmployeeResponse(),
            currentYearNumber = forYear,
            currentMonthNumber = forMonth,
            currentMonthWorkingStartDate = DateUtils.getEpoch(employeeWorkingDetailsForMonthWithDate.startDateTime),
            currentMonthWorkingEndDate = DateUtils.getEpoch(employeeWorkingDetailsForMonthWithDate.endDateTime),
            currentMonthWorkingDays = employeeWorkingDetailsForMonthWithDate.workingDays,
            currentMonthActualSalary = employee.salaryAmountInPaisa,
            currentMonthPaidSalary = getTotalAMount(monthlyPaymentsResponse, forMonth, forYear, MonthlyPaymentType.SALARY),
            currentMonthPayments = getTotalAMount(monthlyPaymentsResponse, forMonth, forYear, MonthlyPaymentType.PAYMENT),
            prevMonthMonthNumber = prevMonthMonthNumber,
            prevMonthYearNumber = prevMonthYearNumber,
            prevMonthClosing = getTotalAMount(prevMonthMonthlyPaymentsResponse, prevMonthMonthNumber, prevMonthYearNumber, MonthlyPaymentType.CLOSING),
            dailyPayments = dailyPayments.map { dailyPayment ->
                DailyPaymentResponse(
                    employeeId = employee.id,
                    forDate = dailyPayment.forDate,
                    dateNumber = DateUtils.getDateNumber(dailyPayment.forDate),
                    dateText = DateUtils.getWeekName(dailyPayment.forDate),
                    salaryAmount = dailyPayment.salaryAmount,
                    paymentsAmount = dailyPayment.paymentsAmount,
                    payments = dailyPayment.payments.map { payment ->
                        PaymentMiniDetailsResponse(
                            employeeId = employee.id,
                            companyId = employee.company?.id ?: "",
                            serverId = payment.id,
                            forDate = payment.forDate,
                            paymentType = payment.paymentType,
                            amountInPaisa = payment.amountInPaisa,
                            multiplierUsed = payment.multiplierUsed,
                            addedAt = DateUtils.getEpoch(payment.addedAt),
                            description = payment.description,
                        )
                    }
                )
            }
        )
    }
}
