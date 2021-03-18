package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.CompanyRepository
import com.dukaankhata.server.dto.UserCompaniesResponse
import com.dukaankhata.server.dto.SaveCompanyRequest
import com.dukaankhata.server.dto.SavedCompanyResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.service.CompanyService
import com.dukaankhata.server.service.converter.CompanyServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.UserRoleUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl : CompanyService() {

    @Autowired
    var companyRepository: CompanyRepository? = null

    @Autowired
    val authUtils: AuthUtils? = null

    @Autowired
    val userRoleUtils: UserRoleUtils? = null

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

        company?.let {
            // Save the user role for the person who created the company
            userRoleUtils?.addUserRole(user, company, RoleType.EMPLOYER)
                ?: error("Unable to save user role while creating company")
        }

        return companyServiceConverter?.getSavedCompanyResponse(company);
    }

    override fun getCompany(): SavedCompanyResponse? {
        TODO("Not yet implemented")
    }

    override fun getUserCompanies(phoneNumber: String): UserCompaniesResponse? {
        val user = authUtils?.getUserByPhoneNumber(phoneNumber);
        val companies = user?.let { companyRepository?.findByUser(it) } ?: emptyList()
        return companyServiceConverter?.getCompaniesResponse(companies)
    }
}
