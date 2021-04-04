package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.CompanyRepository
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.SalaryPaymentSchedule
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

    fun findByUser(user: User): List<Company> {
        return companyRepository.findByUser(user)
    }

    fun saveCompany(user: User, name: String, location: String, salaryPaymentSchedule: SalaryPaymentSchedule, workingMinutes: Int): Company {
        val newCompany = Company()
        newCompany.user = user
        newCompany.name = name
        newCompany.location = location
        newCompany.salaryPaymentSchedule = salaryPaymentSchedule
        newCompany.workingMinutes = workingMinutes
        newCompany.totalDueAmountInPaisa = 0
        return companyRepository.save(newCompany)
    }
}
