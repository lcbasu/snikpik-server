package com.server.shop.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.shop.entities.CompanyV3
import com.server.shop.entities.getHeaderBannerMediaDetails
import com.server.shop.entities.getLogoMediaDetails

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyV3Request (
    val logo: MediaDetailsV2? = null,

    val headerBanner: MediaDetailsV2? = null,
    val marketingName: String,
    val legalName: String,
    val dateOfEstablishmentInSeconds: Long
)

data class SavedCompanyV3Response (
    val id: String,
    val logo: MediaDetailsV2? = null,

    val headerBanner: MediaDetailsV2? = null,
    val marketingName: String,
    val legalName: String,
    val dateOfEstablishment: Long
)

fun CompanyV3.toSavedCompanyV3Response(): SavedCompanyV3Response {
    this.apply {
        return SavedCompanyV3Response(
            id = id,
            logo = getLogoMediaDetails(),
            headerBanner = getHeaderBannerMediaDetails(),
            marketingName = marketingName,
            legalName = legalName,
            dateOfEstablishment = DateUtils.getEpoch(dateOfEstablishment)
        )
    }
}
