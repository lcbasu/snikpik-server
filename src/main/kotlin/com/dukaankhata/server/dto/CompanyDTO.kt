package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.enums.DKShopStatus
import com.dukaankhata.server.enums.SalaryPaymentSchedule
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
    var dkShopStatus: DKShopStatus,
    var username: String,
    var totalOrderAmountInPaisa: Long,
    var totalStoreViewCount: Long,
    var totalOrdersCount: Long,
    var totalProductsViewCount: Long,
    var defaultAddressId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserCompaniesResponse(
    val companies: List<SavedCompanyResponse>,
)

fun Company.toSavedCompanyResponse(): SavedCompanyResponse {
    this.apply {
        return SavedCompanyResponse(
            serverId = id,
            name = name,
            location = location,
            salaryPaymentSchedule = salaryPaymentSchedule,
            workingMinutes = workingMinutes,
            userId = user?.id ?: "",
            totalDueAmountInPaisa = totalDueAmountInPaisa,
            dkShopStatus = dkShopStatus ?: DKShopStatus.OFFLINE,
            username = username ?: "",
            totalOrderAmountInPaisa = totalOrderAmountInPaisa ?: 0,
            totalStoreViewCount = totalStoreViewCount ?: 0,
            totalOrdersCount = totalOrdersCount ?: 0,
            totalProductsViewCount = totalProductsViewCount ?: 0,
            defaultAddressId = defaultAddressId ?: ""
        )
    }
}
