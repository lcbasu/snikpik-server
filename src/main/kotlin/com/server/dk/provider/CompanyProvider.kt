package com.server.dk.provider

import com.server.common.provider.UniqueIdProvider
import com.server.dk.dao.CompanyRepository
import com.server.dk.dao.CompanyUsernameRepository
import com.server.dk.dto.*
import com.server.dk.entities.*
import com.server.dk.enums.CategoryGroup
import com.server.dk.enums.DKShopStatus
import com.server.common.enums.ReadableIdPrefix
import com.server.dk.model.convertToString
import com.server.common.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyProvider {

    var MIN_USERNAME_LENGTH = 4

    @Autowired
    private lateinit var companyRepository: CompanyRepository

    @Autowired
    private lateinit var companyUsernameRepository: CompanyUsernameRepository

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

    fun saveCompanyUsername(user: User?, company: Company, username: String): CompanyUsername {
        if (isUsernameAvailable(username).not()) {
            error("Can not save username: $username. It already exists.")
        }
        val newCompanyUsername = CompanyUsername()
        newCompanyUsername.id = username.toLowerCase()
        newCompanyUsername.addedBy = user
        newCompanyUsername.company = company
        val companyUsername = companyUsernameRepository.save(newCompanyUsername)
        company.username = companyUsername.id
        companyRepository.save(company)
        return companyUsername
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
        newCompany.absoluteMobile = saveCompanyRequest.absoluteMobile ?: user.absoluteMobile ?: ""
        newCompany.countryCode = saveCompanyRequest.countryCode ?: user.countryCode ?: ""
        newCompany.logo = saveCompanyRequest.logo?.convertToString() ?: ""
        val company = companyRepository.save(newCompany)
        val companyUsername = autoGenerateCompanyUsername(company)
        return if (companyUsername != null) companyUsername.company!! else company
    }

    private fun getUsernamePrefixFromCompanyName(company: Company): String {
        // Keep maximum length to be some constant
        val maxLengthOfAutomatedPrefix = 10
        return CommonUtils.getStringWithOnlyCharOrDigit(company.name).take(maxLengthOfAutomatedPrefix)
    }

    private fun getUsernameFromCompanyName(company: Company): String {
        val prefix = getUsernamePrefixFromCompanyName(company)
        var currentCount = 0
        val maxTryOutCount = 10
        while (currentCount < maxTryOutCount) {
            currentCount += 1
            val currentUsername = try {
                uniqueIdProvider.getUniqueId(
                    prefix = prefix,
                    minLength = prefix.length,
                    maxLength = prefix.length + currentCount)
            } catch (e: Exception) {
                ""
            }
            if (currentUsername.isNotEmpty() && isUsernameAvailable(currentUsername)) {
                return currentUsername
            }
        }
        error("Can not generate a suitable username for ${company.id}")
    }

    fun autoGenerateCompanyUsername(company: Company): CompanyUsername? {
        return try {
            val username = getUsernameFromCompanyName(company)
            saveCompanyUsername(
                user = company.user,
                company = company,
                username = username)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCompanyByUsername(username: String): Company? {
        return try {
            return companyUsernameRepository.findById(username.toLowerCase()).get().company
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCompanyByServerIdOrUsername(companyServerIdOrUsername: String): Company? {
        return getCompany(companyServerIdOrUsername) ?: getCompanyByUsername(companyServerIdOrUsername)
    }

    fun isUsernameAvailable(username: String): Boolean {
        return try {
            if (username.length < MIN_USERNAME_LENGTH) {
                return false
            }
            getCompanyByUsername(username) == null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveUsername(user: User, company: Company, username: String): Company? {
        return try {
            saveCompanyUsername(user, company, username).company
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
            company.defaultShopAddress = address
            return companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateName(company: Company, updateCompanyNameRequest: UpdateCompanyNameRequest): Company? {
        return try {
            company.name = updateCompanyNameRequest.newName
            return companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateMobile(company: Company, updateCompanyMobileRequest: UpdateCompanyMobileRequest): Company? {
        return try {
            company.absoluteMobile = updateCompanyMobileRequest.absoluteMobile
            company.countryCode = updateCompanyMobileRequest.countryCode
            return companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateLogo(company: Company, updateCompanyLogoRequest: UpdateCompanyLogoRequest): Company? {
        return try {
            company.logo = updateCompanyLogoRequest.logo.convertToString()
            return companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateCategory(company: Company, request: UpdateCompanyCategoryGroupRequest): Company? {
        return try {
            company.categoryGroup = CategoryGroup.valueOf(request.newCategoryGroupId.name)
            return companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun increaseClick(savedEntityTracking: EntityTracking) {
        val company = savedEntityTracking.company ?: return
        try {
            company.totalStoreClickCount = (company.totalStoreClickCount ?: 0) + 1
            companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun increaseView(savedEntityTracking: EntityTracking) {
        val company = savedEntityTracking.company ?: return
        try {
            company.totalStoreViewCount = (company.totalStoreViewCount ?: 0) + 1
            companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun increaseCompanyProductClick(savedEntityTracking: EntityTracking) {
        val product = savedEntityTracking.product ?: return
        val company = product.company ?: return
        try {
            company.totalProductsClickCount = (company.totalProductsClickCount ?: 0) + 1
            companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun increaseCompanyProductView(savedEntityTracking: EntityTracking) {
        val product = savedEntityTracking.product ?: return
        val company = product.company ?: return
        try {
            company.totalProductsViewCount = (company.totalProductsViewCount ?: 0) + 1
            companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateOrderDetails(productOrder: ProductOrder) {
        val company = productOrder.company ?: error("Company is required")
        try {
            company.totalOrderAmountInPaisa = (company.totalOrderAmountInPaisa ?: 0) + productOrder.totalPricePayableInPaisa
            company.totalOrdersCount = (company.totalOrdersCount ?: 0) + 1
            company.totalUnitsOrdersCount = (company.totalUnitsOrdersCount ?: 0) + productOrder.cartItems.sumBy { it.totalUnits.toInt() }
            companyRepository.save(company)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
