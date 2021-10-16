package com.server.dk.dto

import com.server.dk.entities.Employee
import com.server.dk.enums.OpeningBalanceType
import com.server.dk.enums.RemovalReasonType
import com.server.dk.enums.SalaryType
import com.server.dk.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveEmployeeRequest(
    val name: String,
    val absoluteMobile: String,
    val countryCode: String,
    val companyId: String,
    val salaryType: SalaryType,
    val salaryCycle: String,
    val salaryAmountInPaisa: Long,
    val openingBalanceType: OpeningBalanceType?,
    val openingBalanceInPaisa: Long,
    val joinedAt: Long?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedEmployeeResponse(
    val serverId: String,
    val name: String,
    val absoluteMobile: String,
    val countryCode: String,
    val company: SavedCompanyResponse,
    val salaryType: SalaryType,
    val salaryCycle: String,
    val salaryAmountInPaisa: Long,
//    val openingBalanceType: OpeningBalanceType,
//    val openingBalanceInPaisa: Long,
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateEmployeeJoiningDateRequest(
    val employeeId: String,
    val newJoiningTime: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RemoveEmployeeRequest(
    val employeeId: String,
    val removalReasonType: RemovalReasonType,
    val description: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SalarySlipResponse(
    val employee: SavedEmployeeResponse,
    val startDate: String,
    val endDate: String,
    val salarySlipUrl: String
)


fun Employee.toSavedEmployeeResponse(): SavedEmployeeResponse {
    this.apply {
        return SavedEmployeeResponse(
            serverId = id,
            name = name,
            absoluteMobile = absoluteMobile,
            countryCode = countryCode,
            company = company!!.toSavedCompanyResponse(),
            salaryType = salaryType,
            salaryCycle = salaryCycle,
            salaryAmountInPaisa = salaryAmountInPaisa,
            balanceInPaisaTillNow = balanceInPaisaTillNow,
            isActive = leftAt == null,
            joinedAt = DateUtils.getEpoch(joinedAt),
            leftAt = DateUtils.getEpoch(leftAt)
        )
    }
}
