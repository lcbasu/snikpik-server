package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.enums.OpeningBalanceType
import com.dukaankhata.server.enums.RemovalReasonType
import com.dukaankhata.server.enums.SalaryType
import com.dukaankhata.server.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveEmployeeRequest(
    val name: String,
    val phoneNumber: String,
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
    val phoneNumber: String,
    val companyId: String,
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
            phoneNumber = phoneNumber,
            companyId = company?.id ?: "-1",
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
