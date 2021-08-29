package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveCompanyRequest
import com.dukaankhata.server.dto.SavedCompanyResponse
import com.dukaankhata.server.dto.UserCompaniesResponse
import com.dukaankhata.server.dto.toSavedCompanyResponse
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.service.CompanyService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.CompanyProvider
import com.dukaankhata.server.provider.UserRoleProvider
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

    override fun getUserCompanies(phoneNumber: String): UserCompaniesResponse? {
        val user = authProvider.getUserByMobile(phoneNumber);
        val companies = user?.let { companyProvider.findByUser(it) } ?: emptyList()
        return UserCompaniesResponse(companies = companies.map { it.toSavedCompanyResponse() })
    }
}
