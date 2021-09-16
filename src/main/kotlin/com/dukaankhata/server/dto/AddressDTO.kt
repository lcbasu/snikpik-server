package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Address
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveAddressRequest(
    val name: String = "",
    val phone: String = "",
    val house: String = "",
    val roadName: String = "",
    val type: String = "",
    val zipcode: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val googleCode: String = "", // Unique code for that location
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

data class SavedAddressResponse(
    val serverId: String = "",
    val name: String = "",
    val phone: String = "",
    val house: String = "",
    val roadName: String = "",
    val type: String = "",
    val zipcode: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val googleCode: String = "", // Unique code for that location
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

fun Address.toSavedAddressResponse(): SavedAddressResponse {
    this.apply {
        return SavedAddressResponse(
            serverId = id,
            name = "Mr. Bing",
            phone = "+91-12345678",
            house = line1 ?: "",
            roadName = line2 ?: "",
            type = "Address Type",
            zipcode = zipcode ?: "",
            city = city ?: "",
            state = state ?: "",
            country = country ?: "",
            googleCode = googleCode ?: "",
            latitude = latitude,
            longitude = longitude,
        )
    }
}
