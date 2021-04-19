package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class CompanyService {
    abstract fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse?
    abstract fun getCompany(): SavedCompanyResponse?
    abstract fun getUserCompanies(phoneNumber: String): UserCompaniesResponse?
    abstract fun saveUsername(saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse?
    abstract fun isUsernameAvailable(username: String): UsernameAvailableResponse?
    abstract fun takeShopOffline(takeShopOfflineRequest: TakeShopOfflineRequest): TakeShopOfflineResponse?
    abstract fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse?
}
