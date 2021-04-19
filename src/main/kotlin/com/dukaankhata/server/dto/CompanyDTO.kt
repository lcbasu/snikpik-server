package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.enums.SalaryPaymentSchedule
import com.dukaankhata.server.enums.TakeShopOnlineAfter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyRequest(
    val name: String,
    val location: String,
    val salaryPaymentSchedule: SalaryPaymentSchedule,
    val workingMinutes: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCompanyResponse(
    val serverId: String,
    val name: String,
    val location: String,
    val salaryPaymentSchedule: SalaryPaymentSchedule,
    val workingMinutes: Int,
    val totalDueAmountInPaisa: Long,
    val userId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserCompaniesResponse(
    val companies: List<SavedCompanyResponse>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UsernameAvailableResponse(
    val available: Boolean = false,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveUsernameRequest(
    val companyId: Long,
    val username: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveUsernameResponse(
    val available: Boolean = false,
    val company: SavedCompanyResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOfflineRequest(
    val companyId: Long,
    val takeShopOnlineAfter: TakeShopOnlineAfter,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOfflineResponse(
    val takeShopOnlineAfter: TakeShopOnlineAfter? = null,
    val company: SavedCompanyResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyAddressRequest(
    val companyId: Long,
    val name: String = "",
    val address: SaveAddressRequest,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCompanyAddressResponse(
    val company: SavedCompanyResponse,
    val address: SavedAddressResponse,
)

fun Company.toSavedCompanyResponse(): SavedCompanyResponse {
    this.apply {
        return SavedCompanyResponse(
            serverId = id.toString(),
            name = name,
            location = location,
            salaryPaymentSchedule = salaryPaymentSchedule,
            workingMinutes = workingMinutes,
            userId = user?.id ?: "",
            totalDueAmountInPaisa = totalDueAmountInPaisa)
    }
}
