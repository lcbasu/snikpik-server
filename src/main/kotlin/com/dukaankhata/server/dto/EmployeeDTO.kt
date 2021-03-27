package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.OpeningBalanceType
import com.dukaankhata.server.enums.SalaryType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveEmployeeRequest(
    val name: String,
    val phoneNumber: String,
    val companyId: Long,
    val salaryType: SalaryType,
    val salaryAmountInPaisa: Long,
    val openingBalanceType: OpeningBalanceType?,
    val openingBalanceInPaisa: Long,
    val balanceInPaisaTillNow: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedEmployeeResponse(
    val serverId: String,
    val name: String,
    val phoneNumber: String,
    val companyId: String,
    val salaryType: SalaryType,
    val salaryAmountInPaisa: Long,
    val openingBalanceType: OpeningBalanceType,
    val openingBalanceInPaisa: Long,
    val balanceInPaisaTillNow: Long,
    val isActive: Boolean,
    val joinedAt: Long,
    val leftAt: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CompanyEmployeesResponse(
    val company: SavedCompanyResponse,
    val employees: List<SavedEmployeeResponse>
)