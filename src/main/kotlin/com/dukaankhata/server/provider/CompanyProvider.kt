package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.CompanyRepository
import com.dukaankhata.server.dto.SaveCompanyRequest
import com.dukaankhata.server.entities.Address
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.DKShopStatus
import com.dukaankhata.server.enums.ReadableIdPrefix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyProvider {

    @Autowired
    private lateinit var companyRepository: CompanyRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getCompany(companyId: String): Company? =
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

    fun saveCompany(user: User, saveCompanyRequest: SaveCompanyRequest): Company {
        val newCompany = Company()
        newCompany.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.COM.name)
        newCompany.user = user
        newCompany.name = saveCompanyRequest.name
        newCompany.location = saveCompanyRequest.location
        newCompany.salaryPaymentSchedule = saveCompanyRequest.salaryPaymentSchedule
        newCompany.workingMinutes = saveCompanyRequest.workingMinutes
        newCompany.totalDueAmountInPaisa = 0
        newCompany.categoryGroup = saveCompanyRequest.categoryGroup
        return companyRepository.save(newCompany)
    }

    fun getCompanyByUsername(username: String): Company? {
        return try {
            return companyRepository.findByUsername(username)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isUsernameAvailable(username: String): Boolean {
        return try {
            getCompanyByUsername(username) == null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveUsername(company: Company, username: String): Company? {
        return try {
            company.username = username
            companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun takeShopOffline(company: Company): Company? {
        return try {
            company.dkShopStatus = DKShopStatus.OFFLINE
            companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun takeShopOnline(company: Company): Company? {
        return try {
            company.dkShopStatus = DKShopStatus.ONLINE
            companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateCompanyDefaultAddress(company: Company, address: Address): Company? {
        return try {
            company.defaultAddressId = address.id
            return companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
