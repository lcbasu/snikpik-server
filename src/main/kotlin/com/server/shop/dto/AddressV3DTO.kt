package com.server.shop.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.dto.UserV2PublicMiniDataResponse
import com.server.shop.entities.AddressV3
import com.server.shop.enums.AddressPOCType
import com.server.shop.enums.AddressType

data class UserV3AddressesResponse (
    val user: UserV2PublicMiniDataResponse,
    val addresses: List<SavedAddressV3Response>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveAddressV3Request (
    val addressType: AddressType = AddressType.HOME,
    val pocName: String = "",
    val pocType: AddressPOCType = AddressPOCType.PERSON,
    val email: String?,

    val flatNoBuildingApartmentName: String?,
    val streetLocality: String?,
    val zipcode: String?,

    val absoluteMobile: String?,
    val countryCode: String? = "", // Country code,

    val route: String? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val googleCode: String? = null,
    val completeAddress: String? = "",

    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateAddressV3Request (
    val id: String,
    val addressType: AddressType = AddressType.HOME,
    val pocName: String = "",
    val pocType: AddressPOCType = AddressPOCType.PERSON,
    val email: String?,

    val flatNoBuildingApartmentName: String?,
    val streetLocality: String?,
    val zipcode: String?,

    val absoluteMobile: String?,
    val countryCode: String? = "", // Country code,

    val route: String? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val googleCode: String? = null,
    val completeAddress: String? = "",

    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeleteAddressV3Request (
    val id: String,
)

data class SavedAddressV3Response(
    val id: String = "",
    val addressType: AddressType = AddressType.HOME,
    val pocName: String = "",
    val pocType: AddressPOCType = AddressPOCType.PERSON,
    val email: String,

    val flatNoBuildingApartmentName: String,
    val streetLocality: String,
    val zipcode: String,

    val absoluteMobile: String,
    val countryCode: String? = "", // Country code,

    val route: String? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val googleCode: String? = null,
    val completeAddress: String = "",

    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

fun UpdateAddressV3Request.toSaveAddressV3Request(): SaveAddressV3Request {
    this.apply {
        return SaveAddressV3Request(
            addressType = addressType,
            pocName = pocName,
            pocType = pocType,
            email = email ?: "",

            flatNoBuildingApartmentName = flatNoBuildingApartmentName ?: "",
            streetLocality = streetLocality ?: "",
            zipcode = zipcode ?: "",

            absoluteMobile = absoluteMobile ?: "",
            countryCode = countryCode,

            route = route,
            locality = locality,
            subLocality = subLocality,
            city = city,
            state = state,
            country = country,
            googleCode = googleCode,
            completeAddress = completeAddress,

            latitude = latitude,
            longitude = longitude,
        )
    }
}


fun AddressV3.toSavedAddressV3Response(): SavedAddressV3Response {
    this.apply {
        return SavedAddressV3Response(
            id = id,
            addressType = addressType,
            pocName = pocName,
            pocType = pocType,
            email = email ?: "",

            flatNoBuildingApartmentName = flatNoBuildingApartmentName ?: "",
            streetLocality = streetLocality ?: "",
            zipcode = zipcode ?: "",

            absoluteMobile = absoluteMobile ?: "",
            countryCode = countryCode,

            route = route,
            locality = locality,
            subLocality = subLocality,
            city = city,
            state = state,
            country = country,
            googleCode = googleCode,
            completeAddress = completeAddress,

            latitude = latitude,
            longitude = longitude,
        )
    }
}
