package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.PaymentRepository
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
        val payment = Payment()
        payment.company = company
        payment.employee = employee
        payment.paymentType = savePaymentRequest.paymentType
        payment.addedBy = addedBy
        payment.amountInPaisa = savePaymentRequest.amountInPaisa
        payment.description = savePaymentRequest.description
        payment.forDate = savePaymentRequest.forDate
        payment.multiplierUsed = savePaymentRequest.multiplierUsed
        payment.addedAt = DateUtils.parseEpochInMilliseconds(savePaymentRequest.addedAt)
        return paymentRepository.save(payment)
    }
}
