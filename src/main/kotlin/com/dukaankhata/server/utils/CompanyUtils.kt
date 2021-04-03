package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.CompanyRepository
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyUtils {

    @Autowired
    private lateinit var companyRepository: CompanyRepository

    fun getCompany(companyId: Long): Company? =
        try {
            companyRepository.findById(companyId).get()
        } catch (e: Exception) {
            null
        }

    fun updateCompany(payment: Payment) : Company {
        val company = payment.company ?: error("Payment should always have a company object")
        // -1 is used as this payment is EXACTLY opposite of payment to payment to employee
        company.totalDueAmountInPaisa += -1 * (payment.multiplierUsed * payment.amountInPaisa)
        return companyRepository.save(company)
    }
}
