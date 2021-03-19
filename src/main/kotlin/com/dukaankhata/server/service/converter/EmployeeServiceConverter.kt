package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.CompanyEmployeesResponse
import com.dukaankhata.server.dto.SavedEmployeeResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.enums.OpeningBalanceType
import com.dukaankhata.server.enums.SalaryType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.ZoneOffset

@Component
class EmployeeServiceConverter {

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    fun getSavedEmployeeResponse(employee: Employee?): SavedEmployeeResponse {
        return SavedEmployeeResponse(
            serverId = employee?.id ?: -1,
            name = employee?.name ?: "",
            phoneNumber = employee?.phoneNumber ?: "",
            companyId = employee?.company?.id ?: -1,
            salaryType = employee?.salaryType ?: SalaryType.MONTHLY,
            salaryAmountInPaisa = employee?.salaryAmountInPaisa ?: 0,
            openingBalanceInPaisa = employee?.openingBalanceInPaisa ?: 0,
            openingBalanceType = employee?.openingBalanceType ?: OpeningBalanceType.ADVANCE,
            balanceInPaisaTillNow = employee?.balanceInPaisaTillNow ?: 0,
            isActive = employee?.leftAt === null,
            joinedAt = employee?.joinedAt?.toEpochSecond(ZoneOffset.UTC) ?: 0,
            leftAt = employee?.leftAt?.toEpochSecond(ZoneOffset.UTC) ?: 0,
        )
    }

    fun getCompanyEmployeesResponse(company: Company, employees: List<Employee>): CompanyEmployeesResponse {
        return CompanyEmployeesResponse(
            company = companyServiceConverter.getSavedCompanyResponse(company),
            employees = employees.map {
            getSavedEmployeeResponse(it)
        })
    }

}
