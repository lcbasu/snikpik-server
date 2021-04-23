package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SaveCompanyRequest
import com.dukaankhata.server.dto.SavedCompanyResponse
import com.dukaankhata.server.dto.UserCompaniesResponse

abstract class CompanyService {
    abstract fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse?
    abstract fun getCompany(): SavedCompanyResponse?
    abstract fun getUserCompanies(phoneNumber: String): UserCompaniesResponse?
}
