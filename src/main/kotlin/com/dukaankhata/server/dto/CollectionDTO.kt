package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.entities.getMediaDetails
import com.dukaankhata.server.model.MediaDetails
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCollectionRequest(
    val companyId: String,
    var title: String,
    var subTitle: String?,
    val mediaDetails: MediaDetails,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCollectionResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    var title: String,
    var subTitle: String?,
    val mediaDetails: MediaDetails,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllCollectionsResponse(
    val collections: List<SavedCollectionResponse>
)

fun Collection.toSavedCollectionResponse(): SavedCollectionResponse {
    this.apply {
        return SavedCollectionResponse(
            serverId = id,
            company = company!!.toSavedCompanyResponse(),
            title = title,
            subTitle = subTitle,
            mediaDetails = getMediaDetails()
        )
    }
}
