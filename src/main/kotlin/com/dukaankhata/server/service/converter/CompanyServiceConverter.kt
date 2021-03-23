package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.UserCompaniesResponse
import com.dukaankhata.server.dto.SavedCompanyResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.enums.SalaryPaymentSchedule
import org.springframework.stereotype.Component

@Component
class CompanyServiceConverter {

    fun getSavedCompanyResponse(company: Company?): SavedCompanyResponse {
        return SavedCompanyResponse(
            serverId = company?.id?.toString() ?: "-1",
            name = company?.name ?: "",
            location = company?.location ?: "",
            salaryPaymentSchedule = company?.salaryPaymentSchedule ?: SalaryPaymentSchedule.LAST_DAY_OF_MONTH,
            workingMinutes = company?.workingMinutes ?: 0,
            userId = company?.user?.id ?: "",
            totalDueAmountInPaisa = company?.totalDueAmountInPaisa ?: 0)
    }

    fun getCompaniesResponse(companies: List<Company>): UserCompaniesResponse {
        return UserCompaniesResponse(companies = companies.map { getSavedCompanyResponse(it) })
    }

}
