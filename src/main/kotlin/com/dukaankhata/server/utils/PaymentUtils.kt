package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.PaymentRepository
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.PaymentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PaymentUtils {

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    fun getPayment(paymentId: Long): Payment? =
        try {
            paymentRepository.findById(paymentId).get()
        } catch (e: Exception) {
            null
        }

    fun savePayment(addedBy: User, company: Company, employee: Employee, savePaymentRequest: SavePaymentRequest) : Payment {
        /**
         *
         * +1 -> EMPLOYEE owes to EMPLOYER. This will REDUCE the amount of money owed by employer to employee
         * -1 -> EMPLOYER owes to EMPLOYEE. This will INCREASE the amount of money owed by employer to employee
         *
         * */
        val multiplierUsed = when (savePaymentRequest.paymentType) {
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
            PaymentType.NONE -> 0
        }

        val payment = Payment()
        payment.company = company
        payment.employee = employee
        payment.paymentType = savePaymentRequest.paymentType
        payment.addedBy = addedBy
        payment.amountInPaisa = savePaymentRequest.amountInPaisa
        payment.description = savePaymentRequest.description
        payment.forDate = savePaymentRequest.forDate
        payment.multiplierUsed = multiplierUsed
        payment.addedAt = DateUtils.dateTimeNow()
        return paymentRepository.save(payment)
    }

    fun getAllPaymentsBetweenGivenTimes(companyId: Long, startTime: LocalDateTime, endTime: LocalDateTime): List<Payment> {
        return paymentRepository.getAllPaymentsBetweenGivenTimes(companyId, startTime, endTime)
    }
}
