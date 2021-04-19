package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Address
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveAddressRequest(
    val line1: String = "",
    val line2: String = "",
    val zipcode: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val googleCode: String = "", // Unique code for that location
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String = ""
)

data class SavedAddressResponse(
    val serverId: String = "",
    val line1: String = "",
    val line2: String = "",
    val zipcode: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val googleCode: String = "", // Unique code for that location
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String = ""
)

fun Address.toSavedAddressResponse(): SavedAddressResponse {
    this.apply {
        return SavedAddressResponse(
            serverId = id.toString(),
            line1 = line1 ?: "",
            line2 = line2 ?: "",
            zipcode = zipcode ?: "",
            city = city ?: "",
            state = state ?: "",
            country = country ?: "",
            googleCode = googleCode ?: "",
            latitude = latitude,
            longitude = longitude,
            phone = phone ?: ""
        )
    }
}
