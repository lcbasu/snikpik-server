package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.DateUtils
import com.server.ud.entities.location.Location
import com.server.ud.enums.LocationFor

data class CityLocationData(
    val city: String,
    val state: String,
    val country: String,
    val countryCode: String,
    val zipcode: String,
    val latitude: Double,
    val longitude: Double,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class IPLocationData (
    val city: String?,
    val state: String?,
    val country: String?,
    val countryCode: String?,
    val zipcode: String?,
    val latitude: Double?,
    val longitude: Double?,
)

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
            createdAt = DateUtils.getEpoch(createdAt),
            zipcode = zipcode,
            googlePlaceId = googlePlaceId,
            name = name,
            lat = lat,
            lng = lng,
            locationFor = locationFor
        )
    }
}

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

fun CityLocationData.toSaveLocationRequest(locationFor: LocationFor): SaveLocationRequest {
    this.apply {
        return SaveLocationRequest(
            locationFor = locationFor,
            zipcode = zipcode,
            googlePlaceId = null,
            name = "$city, $state",
            lat = latitude,
            lng = longitude,
        )
    }
}

