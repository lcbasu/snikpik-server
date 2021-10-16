package com.server.dk.service.impl

import com.server.dk.dto.*
import com.server.dk.enums.RoleType
import com.server.dk.enums.TakeShopOnlineAfter
import com.server.dk.provider.AddressProvider
import com.server.dk.provider.AuthProvider
import com.server.dk.provider.CompanyProvider
import com.server.dk.provider.UserRoleProvider
import com.server.dk.service.CompanyService
import com.server.dk.service.schedule.TakeShopOnlineSchedulerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl : CompanyService() {

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var userRoleProvider: UserRoleProvider

    @Autowired
    private lateinit var addressProvider: AddressProvider

    @Autowired
    private lateinit var takeShopOnlineSchedulerService: TakeShopOnlineSchedulerService

    override fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse? {
        val user = authProvider.getRequestUserEntity() ?: error("Only saved users are allowed to create shops.")
        val company = companyProvider.saveCompany(
            user = user,
            saveCompanyRequest = saveCompanyRequest
        )

        company.let {
            // Save the user role for the person who created the company
            userRoleProvider.addUserRole(user, company, RoleType.EMPLOYER)
                ?: error("Unable to save user role while creating company")
        }

        return company.toSavedCompanyResponse();
    }

    override fun getCompany(companyServerIdOrUsername: String): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyServerIdOrUsername
        )
        val company = requestContext.company ?: error("Company is required")
        return company.toSavedCompanyResponse()
    }

    override fun getUserCompanies(absoluteMobile: String): UserCompaniesResponse? {
        val user = authProvider.getUserByMobile(absoluteMobile);
        val companies = user?.let { companyProvider.findByUser(it) } ?: emptyList()
        return UserCompaniesResponse(companies = companies.map { it.toSavedCompanyResponse() })
    }

//    override fun saveUsername(saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse? {
//        val requestContext = authProvider.validateRequest(
//            companyServerIdOrUsername = saveUsernameRequest.companyId,
//            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
//        )
//        val company = requestContext.company ?: error("Company is required")
//
//        if (company.username != null && company.username!!.isNotBlank()) {
//            error("You can not edit username once it is added")
//        }
//
//        val isAvailable = companyProvider.isUsernameAvailable(saveUsernameRequest.username)
//
//        if (isAvailable) {
//            val updatedCompany = companyProvider.saveUsername(requestContext.user, company, saveUsernameRequest.username) ?: error("Saving username failed")
//            return SaveUsernameResponse(
//                available = true,
//                company = updatedCompany.toSavedCompanyResponse()
//            )
//        }
//        return SaveUsernameResponse(
//            available = false,
//            company = null
//        )
//    }

    override fun isUsernameAvailable(username: String): UsernameAvailableResponse? {
        // To verify if the user is logged in
        authProvider.validateRequest()

        return UsernameAvailableResponse(companyProvider.isUsernameAvailable(username))
    }

    override fun takeShopOffline(takeShopOfflineRequest: TakeShopOfflineRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = takeShopOfflineRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val updatedCompany = companyProvider.takeShopOffline(company) ?: error("Company update failed")

        if (takeShopOfflineRequest.takeShopOnlineAfter != TakeShopOnlineAfter.MANUALLY) {
            takeShopOnlineSchedulerService.takeShopOnline(company, takeShopOfflineRequest.takeShopOnlineAfter)
        }

        return updatedCompany.toSavedCompanyResponse()
    }

    override fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = saveCompanyAddressRequest.companyServerIdOrUsername,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val companyAddress = addressProvider.saveCompanyAddress(company, saveCompanyAddressRequest.name, saveCompanyAddressRequest.address) ?: error("Error while saveing company address")
        val newAddress = companyAddress.address ?: error("Address should always be present for companyAddress")
        val updatedCompany = companyProvider.updateCompanyDefaultAddress(company, newAddress) ?: error("Error while updating default address for comany")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun takeShopOnlineNow(takeShopOnlineNowRequest: TakeShopOnlineNowRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = takeShopOnlineNowRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.takeShopOnline(company) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun getAddresses(companyServerIdOrUsername: String): CompanyAddressesResponse {
        TODO("Not yet implemented")
    }

    override fun updateName(updateCompanyNameRequest: UpdateCompanyNameRequest): SavedCompanyResponse? {
        if (updateCompanyNameRequest.newName.trim().isBlank()) {
            error("Invalid name provided for the company");
        }
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = updateCompanyNameRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.updateName(company, updateCompanyNameRequest) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun updateMobile(updateCompanyMobileRequest: UpdateCompanyMobileRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = updateCompanyMobileRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.updateMobile(company, updateCompanyMobileRequest) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun updateLogo(updateCompanyLogoRequest: UpdateCompanyLogoRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = updateCompanyLogoRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.updateLogo(company, updateCompanyLogoRequest) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun updateUsername(updateCompanyUsernameRequest: UpdateCompanyUsernameRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = updateCompanyUsernameRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val isAvailable = companyProvider.isUsernameAvailable(updateCompanyUsernameRequest.newUsername)

        if (isAvailable) {
            val updatedCompany = companyProvider.saveUsername(requestContext.user, company, updateCompanyUsernameRequest.newUsername) ?: error("Saving username failed")
            return updatedCompany.toSavedCompanyResponse()
        }
        return company.toSavedCompanyResponse()
    }

    override fun updateCategoryGroup(request: UpdateCompanyCategoryGroupRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = request.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.updateCategory(company, request) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }
}
