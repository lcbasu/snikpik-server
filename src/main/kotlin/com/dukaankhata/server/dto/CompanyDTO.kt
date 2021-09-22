package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.enums.CategoryGroup
import com.dukaankhata.server.enums.DKShopStatus
import com.dukaankhata.server.enums.SalaryPaymentSchedule
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyRequest(
    val name: String,
    val location: String,
    val salaryPaymentSchedule: SalaryPaymentSchedule,
    val workingMinutes: Int,
    val categoryGroup: CategoryGroup? = CategoryGroup.General,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCompanyResponse(
    val serverId: String,
    val name: String,
    val location: String,
    val salaryPaymentSchedule: SalaryPaymentSchedule,
    val workingMinutes: Int,
    val categoryGroup: CategoryGroupResponse,
    val totalDueAmountInPaisa: Long,
    val userId: String,
    val dkShopStatus: DKShopStatus,
    val username: String,
    val totalOrderAmountInPaisa: Long,
    val totalStoreViewCount: Long,
    val totalOrdersCount: Long,
    val totalProductsViewCount: Long,
    val defaultAddressId: String?,
    val defaultShopAddress: SavedAddressResponse?,
)

data class CompanyAddressesResponse(
    val company: SavedCompanyResponse,
    val addresses: List<SavedAddressResponse>
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
            defaultAddressId = defaultAddressId ?: "",
            defaultShopAddress = defaultShopAddress?.toSavedAddressResponse(),
            categoryGroup = (categoryGroup ?: CategoryGroup.General).let {
                CategoryGroupResponse(
                    id = it.id,
                    displayName = it.displayName,
                    description = it.description,
                    mediaDetails = it.mediaDetails
                )
            },
        )
    }
}
