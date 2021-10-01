package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.enums.TakeShopOnlineAfter
import com.dukaankhata.server.provider.AddressProvider
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.CompanyProvider
import com.dukaankhata.server.provider.UserRoleProvider
import com.dukaankhata.server.service.CompanyService
import com.dukaankhata.server.service.schedule.TakeShopOnlineSchedulerService
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

    override fun getCompany(): SavedCompanyResponse? {
        TODO("Not yet implemented")
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

    override fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = saveCompanyAddressRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val companyAddress = addressProvider.saveCompanyAddress(company, saveCompanyAddressRequest.name, saveCompanyAddressRequest.address) ?: error("Error while saveing company address")
        val newAddress = companyAddress.address ?: error("Address should always be present for companyAddress")
        val updatedCompany = companyProvider.updateCompanyDefaultAddress(company, newAddress) ?: error("Error while updating default address for comany")

        return SavedCompanyAddressResponse(
            company = updatedCompany.toSavedCompanyResponse(),
            address = newAddress.toSavedAddressResponse()
        )
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

    override fun updateName(updateNameRequest: UpdateNameRequest): SavedCompanyResponse? {
        if (updateNameRequest.newName.trim().isBlank()) {
            error("Invalid name provided for the company");
        }
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = updateNameRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.updateName(company, updateNameRequest) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun updateMobile(updateMobileRequest: UpdateMobileRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = updateMobileRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.updateMobile(company, updateMobileRequest) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun updateLogo(updateLogoRequest: UpdateLogoRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = updateLogoRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.updateLogo(company, updateLogoRequest) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun updateUsername(updateUsernameRequest: UpdateUsernameRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = updateUsernameRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val isAvailable = companyProvider.isUsernameAvailable(updateUsernameRequest.newUsername)

        if (isAvailable) {
            val updatedCompany = companyProvider.saveUsername(requestContext.user, company, updateUsernameRequest.newUsername) ?: error("Saving username failed")
            return updatedCompany.toSavedCompanyResponse()
        }
        return company.toSavedCompanyResponse()
    }
}
