package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveCompanyRequest
import com.dukaankhata.server.dto.SavedCompanyResponse
import com.dukaankhata.server.dto.UserCompaniesResponse
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.service.CompanyService
import com.dukaankhata.server.service.converter.CompanyServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CompanyUtils
import com.dukaankhata.server.utils.UserRoleUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl : CompanyService() {

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    @Autowired
    val companyServiceConverter: CompanyServiceConverter? = null

    override fun saveCompany(saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse? {
        val user = authUtils.getRequestUserEntity() ?: return null
        val company = companyUtils.saveCompany(
            user = user,
            name = saveCompanyRequest.name,
            location = saveCompanyRequest.location,
            salaryPaymentSchedule = saveCompanyRequest.salaryPaymentSchedule,
            workingMinutes = saveCompanyRequest.workingMinutes
        )

        company.let {
            // Save the user role for the person who created the company
            userRoleUtils.addUserRole(user, company, RoleType.EMPLOYER)
                ?: error("Unable to save user role while creating company")
        }

        return companyServiceConverter?.getSavedCompanyResponse(company);
    }

    override fun getCompany(): SavedCompanyResponse? {
        TODO("Not yet implemented")
    }

    override fun getUserCompanies(phoneNumber: String): UserCompaniesResponse? {
        val user = authUtils.getUserByPhoneNumber(phoneNumber);
        val companies = user?.let { companyUtils.findByUser(it) } ?: emptyList()
        return companyServiceConverter?.getCompaniesResponse(companies)
    }
}
