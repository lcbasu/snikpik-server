package com.server.dk.service

import com.server.dk.dto.*

abstract class CompanyService {
    abstract fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse?
    abstract fun getCompany(companyServerIdOrUsername: String): SavedCompanyResponse?
    abstract fun getUserCompanies(absoluteMobile: String): UserCompaniesResponse?
    abstract fun isUsernameAvailable(username: String): UsernameAvailableResponse?
//    abstract fun saveUsername(saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse?
    abstract fun takeShopOffline(takeShopOfflineRequest: TakeShopOfflineRequest): SavedCompanyResponse?
    abstract fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyResponse?
    abstract fun takeShopOnlineNow(takeShopOnlineNowRequest: TakeShopOnlineNowRequest): SavedCompanyResponse?
    abstract fun getAddresses(companyServerIdOrUsername: String): CompanyAddressesResponse
    abstract fun updateName(updateCompanyNameRequest: UpdateCompanyNameRequest): SavedCompanyResponse?
    abstract fun updateMobile(updateCompanyMobileRequest: UpdateCompanyMobileRequest): SavedCompanyResponse?
    abstract fun updateLogo(updateCompanyLogoRequest: UpdateCompanyLogoRequest): SavedCompanyResponse?
    abstract fun updateUsername(updateCompanyUsernameRequest: UpdateCompanyUsernameRequest): SavedCompanyResponse?
    abstract fun updateCategoryGroup(request: UpdateCompanyCategoryGroupRequest): SavedCompanyResponse?
    abstract fun getCustomersData(companyServerIdOrUsername: String): CustomersDataResponse
}
