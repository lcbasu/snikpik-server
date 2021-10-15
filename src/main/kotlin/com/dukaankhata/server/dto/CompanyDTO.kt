package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.getLogoDetails
import com.dukaankhata.server.enums.*
import com.dukaankhata.server.model.MediaDetails
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UsernameAvailableResponse(
    val available: Boolean = false,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCompanyUsernameRequest(
    val companyId: String,
    val newUsername: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCompanyNameRequest(
    val companyId: String,
    val newName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCompanyMobileRequest(
    val companyId: String,
    val absoluteMobile: String,
    val countryCode: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCompanyCategoryGroupRequest(
    val companyId: String,
    val newCategoryGroupId: CategoryGroupId,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateCompanyLogoRequest(
    val companyId: String,
    val logo: MediaDetails,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOnlineNowRequest(
    val companyId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOfflineRequest(
    val companyId: String,
    val takeShopOnlineAfter: TakeShopOnlineAfter,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TakeShopOfflineResponse(
    val takeShopOnlineAfter: TakeShopOnlineAfter? = null,
    val company: SavedCompanyResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyAddressRequest(
    val companyServerIdOrUsername: String,
    val name: String = "",
    val address: SaveAddressRequest,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyRequest(
    val name: String,
    val location: String,
    val salaryPaymentSchedule: SalaryPaymentSchedule,
    val workingMinutes: Int,
    val absoluteMobile: String?, // Use user logged in mobile number
    val countryCode: String?,
    val categoryGroup: CategoryGroup? = CategoryGroup.General,
    val logo: MediaDetails? = null,
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
    var totalStoreClickCount: Long,
    var totalUnitsOrdersCount: Long,
    var totalProductsClickCount: Long,
    val totalStoreViewCount: Long,
    val totalOrdersCount: Long,
    val totalProductsViewCount: Long,
    val defaultAddressId: String?,
    val defaultShopAddress: SavedAddressResponse?,
    val absoluteMobile: String?,
    val countryCode: String?,
    val logo: MediaDetails? = null,
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
            countryCode = countryCode,
            absoluteMobile = absoluteMobile,
            logo = getLogoDetails(),
            totalStoreClickCount = totalStoreClickCount ?: 0,
            totalUnitsOrdersCount = totalUnitsOrdersCount ?: 0,
            totalProductsClickCount = totalProductsClickCount ?: 0,
        )
    }
}
