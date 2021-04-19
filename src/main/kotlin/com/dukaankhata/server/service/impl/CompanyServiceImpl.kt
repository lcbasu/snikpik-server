package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.enums.TakeShopOnlineAfter
import com.dukaankhata.server.service.CompanyService
import com.dukaankhata.server.service.converter.CompanyServiceConverter
import com.dukaankhata.server.service.schedule.TakeShopOnlineSchedulerService
import com.dukaankhata.server.utils.AddressUtils
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CompanyUtils
import com.dukaankhata.server.utils.UserRoleUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl : CompanyService() {

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    @Autowired
    private lateinit var addressUtils: AddressUtils

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var takeShopOnlineSchedulerService: TakeShopOnlineSchedulerService

    override fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse? {
        val user = authUtils.getRequestUserEntity() ?: return null
        val company = companyUtils.saveCompany(
            user = user,
            name = saveCompanyRequest.name,
            location = saveCompanyRequest.location,
            salaryPaymentSchedule = saveCompanyRequest.salaryPaymentSchedule,
            workingMinutes = saveCompanyRequest.workingMinutes
        )

        company.let {
            // Save the user role for the person who created the company
            userRoleUtils.addUserRole(user, company, RoleType.EMPLOYER)
                ?: error("Unable to save user role while creating company")
        }

        return companyServiceConverter.getSavedCompanyResponse(company);
    }

    override fun getCompany(): SavedCompanyResponse? {
        TODO("Not yet implemented")
    }

    override fun getUserCompanies(phoneNumber: String): UserCompaniesResponse? {
        val user = authUtils.getUserByPhoneNumber(phoneNumber);
        val companies = user?.let { companyUtils.findByUser(it) } ?: emptyList()
        return companyServiceConverter.getCompaniesResponse(companies)
    }

    override fun saveUsername(saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = saveUsernameRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        if (company.username != null && company.username!!.isNotBlank()) {
            error("You can not edit username once it is added")
        }

        val isAvailable = companyUtils.isUsernameAvailable(saveUsernameRequest.username)

        if (isAvailable) {
            val updatedCompany = companyUtils.saveUsername(company, saveUsernameRequest.username) ?: error("Saving username failed")
            return SaveUsernameResponse(
                available = true,
                company = companyServiceConverter.getSavedCompanyResponse(updatedCompany)
            )
        }
        return SaveUsernameResponse(
            available = false,
            company = null
        )
    }

    override fun isUsernameAvailable(username: String): UsernameAvailableResponse? {
        // To verify if the user is logged in
        authUtils.validateRequest()

        return UsernameAvailableResponse(companyUtils.isUsernameAvailable(username))
    }

    override fun takeShopOffline(takeShopOfflineRequest: TakeShopOfflineRequest): TakeShopOfflineResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = takeShopOfflineRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val updatedCompany = companyUtils.takeShopOffline(company)

        if (takeShopOfflineRequest.takeShopOnlineAfter != TakeShopOnlineAfter.MANUALLY) {
            takeShopOnlineSchedulerService.takeShopOnline(company, takeShopOfflineRequest.takeShopOnlineAfter)
        }

        return TakeShopOfflineResponse(
            takeShopOnlineAfter = takeShopOfflineRequest.takeShopOnlineAfter,
            company = companyServiceConverter.getSavedCompanyResponse(updatedCompany)
        )
    }

    override fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = saveCompanyAddressRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val companyAddress = addressUtils.saveCompanyAddress(company, saveCompanyAddressRequest.name, saveCompanyAddressRequest.address) ?: error("Error while saveing company address")
        val newAddress = companyAddress.address ?: error("Address should always be present for companyAddress")
        val updatedCompany = companyUtils.updateCompanyDefaultAddress(company, newAddress) ?: error("Error while updating default address for comany")

        return SavedCompanyAddressResponse(
            company = companyServiceConverter.getSavedCompanyResponse(updatedCompany),
            address = newAddress.toSavedAddressResponse()
        )
    }
}
