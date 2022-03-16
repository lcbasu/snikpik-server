package com.server.shop.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.shop.entities.Brand
import com.server.shop.entities.getHeaderBannerMediaDetails
import com.server.shop.entities.getLogoMediaDetails

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveBrandRequest (
    val companyId: String,

    val marketingName: String,
    val legalName: String,
    val handle: String? = null,
    val logo: MediaDetailsV2? = null,
    val headerBanner: MediaDetailsV2? = null,
    val dateOfEstablishmentInSeconds: Long
)

data class SavedBrandResponse (
    val id: String,
    val handle: String,

    val logo: MediaDetailsV2? = null,
    val headerBanner: MediaDetailsV2? = null,
    val marketingName: String,
    val legalName: String,
    val dateOfEstablishment: Long,

    val company: SavedCompanyV3Response
)

fun Brand.toSavedBrandResponse(): SavedBrandResponse {
    this.apply {
        return SavedBrandResponse(
            id = id,
            handle = handle,
            logo = getLogoMediaDetails(),
            headerBanner = getHeaderBannerMediaDetails(),
            marketingName = marketingName,
            legalName = legalName,
            dateOfEstablishment = DateUtils.getEpoch(dateOfEstablishment),
            company = company!!.toSavedCompanyV3Response()
        )
    }
}
