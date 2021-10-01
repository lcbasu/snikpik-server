package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class CompanyService {
    abstract fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse?
    abstract fun getCompany(): SavedCompanyResponse?
    abstract fun getUserCompanies(absoluteMobile: String): UserCompaniesResponse?
    abstract fun isUsernameAvailable(username: String): UsernameAvailableResponse?
//    abstract fun saveUsername(saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse?
    abstract fun takeShopOffline(takeShopOfflineRequest: TakeShopOfflineRequest): SavedCompanyResponse?
    abstract fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse?
    abstract fun takeShopOnlineNow(takeShopOnlineNowRequest: TakeShopOnlineNowRequest): SavedCompanyResponse?
    abstract fun getAddresses(companyServerIdOrUsername: String): CompanyAddressesResponse
    abstract fun updateName(updateNameRequest: UpdateNameRequest): SavedCompanyResponse?
    abstract fun updateMobile(updateMobileRequest: UpdateMobileRequest): SavedCompanyResponse?
    abstract fun updateLogo(updateLogoRequest: UpdateLogoRequest): SavedCompanyResponse?
    abstract fun updateUsername(updateUsernameRequest: UpdateUsernameRequest): SavedCompanyResponse?
}
