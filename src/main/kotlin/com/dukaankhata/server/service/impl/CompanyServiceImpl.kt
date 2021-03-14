package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.CompanyRepository
import com.dukaankhata.server.dto.SaveCompanyRequest
import com.dukaankhata.server.dto.SavedCompanyResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.service.CompanyService
import com.dukaankhata.server.service.converter.CompanyServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl : CompanyService() {

    @Autowired
    var companyRepository: CompanyRepository? = null

    @Autowired
    val authUtils: AuthUtils? = null

    @Autowired
    val companyServiceConverter: CompanyServiceConverter? = null

    override fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse? {
        val user = authUtils?.getRequestUserEntity() ?: return null
        // Logged in user and the company data user id should always be the same
        val company = companyRepository?.let {
            val newCompany = Company()
            newCompany.name = saveCompanyRequest.name
            newCompany.location = saveCompanyRequest.location
            newCompany.salaryPaymentSchedule = saveCompanyRequest.salaryPaymentSchedule
            newCompany.workingMinutes = saveCompanyRequest.workingMinutes
            newCompany.user = user
            it.save(newCompany)
        }
        return companyServiceConverter?.getSavedCompanyResponse(company);
    }

    override fun getCompany(): SavedCompanyResponse? {
        return super.getCompany()
    }
}
