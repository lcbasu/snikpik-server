package com.dukaankhata.server.service

import com.dukaankhata.server.dto.company.SaveCompanyRequest
import com.dukaankhata.server.dto.company.SavedCompanyResponse

open class CompanyService {
    open fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse? = null
    open fun getCompany(): SavedCompanyResponse? = null
}
