package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.provider.CityLocationData
import com.server.ud.entities.location.Location
import com.server.ud.enums.LocationFor

@JsonIgnoreProperties(ignoreUnknown = true)
data class CityLocationDataResponse(
    val city: String,
    val state: String,
    val country: String,
    val countryCode: String,
    val zipcode: String,
    val latitude: Double,
    val longitude: Double,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CitiesLocationResponse(
    var cities: List<CityLocationDataResponse>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakeLocationRequest(
    var countOfLocation: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveLocationRequest(
    val locationFor: LocationFor,
    val zipcode: String? = null,
    val googlePlaceId: String? = null,
    val name: String? = null,
    val lat: Double? = null,
    val lng:Double? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedLocationResponse(
    var locationId: String,
    val locationFor: LocationFor,
    var userId: String,
    var createdAt: Long,
    val zipcode: String? = null,
    val googlePlaceId: String? = null,
    val name: String? = null,
    val lat: Double? = null,
    val lng:Double? = null,
)

fun Location.toSavedLocationResponse(): SavedLocationResponse {
    this.apply {
        return SavedLocationResponse(
            locationId = locationId,
            userId = userId,
            createdAt = createdAt.toEpochMilli(),
            zipcode = zipcode,
            googlePlaceId = googlePlaceId,
            name = name,
            lat = lat,
            lng = lng,
            locationFor = locationFor
        )
    }
}

val sampleLocationRequests = listOf(
    SaveLocationRequest(
        locationFor = LocationFor.GENERIC_POST,
        zipcode = "560037",
        googlePlaceId = "Ek1Sb2hhbiBKaGFyb2thIElJIFJkLCBLZW1wYXB1cmEsIEJlbGxhbmR1ciwgQmVuZ2FsdXJ1LCBLYXJuYXRha2EgNTYwMDM3LCBJbmRpYSIuKiwKFAoSCYUAWlaVE647EfrOWIxmz_epEhQKEgmRQbo_lBOuOxFTsTfMexw1KA",
        name = "Rohan Jharoka II Rd",
        lat = 12.94826,
        lng = 77.676568,
    ),
    SaveLocationRequest(
        locationFor = LocationFor.GENERIC_POST,
        zipcode = "560071",
        googlePlaceId = "ChIJwab6ORIUrjsRzU_IULQpkIQ",
        name = "Embassy Golf Links Business Park",
        lat = 12.951315,
        lng = 77.646453,
    ),
    SaveLocationRequest(
        locationFor = LocationFor.GENERIC_POST,
        zipcode = "560071",
        googlePlaceId = "ChIJ2-z4UhoUrjsRSb4PrWthy_A",
        name = "Domlur I Stage",
        lat = 12.959381,
        lng = 77.637862,
    ),
    SaveLocationRequest(
        locationFor = LocationFor.GENERIC_POST,
        zipcode = "560037",
        googlePlaceId = "ChIJ62XomDMSrjsRAUc663qoLls",
        name = "Marathahalli Main Rd",
        lat = 12.955728,
        lng = 77.717275,
    ),
    SaveLocationRequest(
        locationFor = LocationFor.GENERIC_POST,
        zipcode = "560025",
        googlePlaceId = "ChIJhYUeuyASrjsRMovHiesaQIw",
        name = "DivyaSree Chambers",
        lat = 12.962098,
        lng = 77.599589,
    ),
    SaveLocationRequest(
        locationFor = LocationFor.GENERIC_POST,
        zipcode = "570001",
        googlePlaceId = "ChIJ-aH5AxFwrzsRDdokoeK6f8M",
        name = "Mysore Palace",
        lat = 12.305163,
        lng = 76.655175,
    )
)

fun CityLocationData.toCityLocationDataResponse(): CityLocationDataResponse {
    this.apply {
        return CityLocationDataResponse(
            city = city,
            state = state,
            country = country,
            countryCode = countryCode,
            zipcode = zipcode,
            latitude = latitude,
            longitude = longitude,
        )
    }
}
