package com.dukaankhata.server.utils

import SalaryReversal
import com.dukaankhata.server.dao.PaymentRepository
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.PaymentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PaymentUtils {

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    fun getPayment(paymentId: Long): Payment? =
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

    fun getAllPaymentsBetweenGivenTimes(companyId: Long, startTime: LocalDateTime, endTime: LocalDateTime): List<Payment> {
        return paymentRepository.getAllPaymentsBetweenGivenTimes(companyId, startTime, endTime)
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
}
