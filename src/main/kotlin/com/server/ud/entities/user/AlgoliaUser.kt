package com.server.ud.entities.user

import com.server.common.dto.AllProfileTypeResponse
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.dto.AllCategoryV2Response
import com.server.ud.entities.post.GeoLoc
import org.springframework.data.cassandra.core.mapping.Column

data class AlgoliaUser (
    val objectID: String,
    val userId: String,
    val createdAt: Long,
    val absoluteMobile: String? = null,
    val countryCode: String? = null,
    val handle: String? = null,
    val email: String? = null,
    @Deprecated("Start using dpV2")val dp: String? = null,
    val dpV2: MediaDetailsV2? = null,
    val uid: String? = "",
    val anonymous: Boolean = false,
    val verified: Boolean = false,
    val profiles: AllProfileTypeResponse = AllProfileTypeResponse(emptyList()),
    val fullName: String? = "",

    // Permanent location info
    val _geoloc: GeoLoc? = null,

    val currentLocationZipcode: String? = null,
    val currentGooglePlaceId: String? = null,
    val currentLocationId: String? = null,
    val currentLocationName: String? = null,
    val currentLocationLat: Double? = null,
    val currentLocationLng: Double? = null,
    val currentLocationLocality: String? = null,
    val currentLocationSubLocality: String? = null,
    val currentLocationRoute: String? = null,
    val currentLocationCity: String? = null,
    val currentLocationState: String? = null,
    val currentLocationCountry: String? = null,
    val currentLocationCountryCode: String? = null,
    val currentLocationCompleteAddress: String? = null,
    val permanentLocationZipcode: String? = null,
    val permanentGooglePlaceId: String? = null,
    val permanentLocationId: String? = null,
    val permanentLocationName: String? = null,
    val permanentLocationLat: Double? = null,
    val permanentLocationLng: Double? = null,
    val permanentLocationLocality: String? = null,
    val permanentLocationSubLocality: String? = null,
    val permanentLocationRoute: String? = null,
    val permanentLocationCity: String? = null,
    val permanentLocationState: String? = null,
    val permanentLocationCountry: String? = null,
    val permanentLocationCountryCode: String? = null,
    val permanentLocationCompleteAddress: String? = null,

    val preferredCategories: AllCategoryV2Response = AllCategoryV2Response(emptyList()),
)

fun UserV2.toAlgoliaUser(): AlgoliaUser {
    this.apply {
        return AlgoliaUser(
            objectID = userId,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),
            absoluteMobile = absoluteMobile,
            countryCode = countryCode,
            handle = handle,
            email = email,
            dpV2 = getMediaDetailsForDP(),
            uid = uid,
            anonymous = anonymous,
            verified = verified,
            profiles = getProfiles(),
            fullName = fullName,
            _geoloc = if (permanentLocationLat != null && permanentLocationLng != null) GeoLoc(
                lat = permanentLocationLat,
                lng = permanentLocationLng
            ) else null,
            preferredCategories = getPreferredCategories(),
            currentLocationZipcode = currentLocationZipcode,
            currentGooglePlaceId = currentGooglePlaceId,
            currentLocationId = currentLocationId,
            currentLocationName = currentLocationName,
            currentLocationLat = currentLocationLat,
            currentLocationLng = currentLocationLng,
            currentLocationLocality = currentLocationLocality,
            currentLocationSubLocality = currentLocationSubLocality,
            currentLocationRoute = currentLocationRoute,
            currentLocationCity = currentLocationCity,
            currentLocationState = currentLocationState,
            currentLocationCountry = currentLocationCountry,
            currentLocationCountryCode = currentLocationCountryCode,
            currentLocationCompleteAddress = currentLocationCompleteAddress,
            permanentLocationZipcode = permanentLocationZipcode,
            permanentGooglePlaceId = permanentGooglePlaceId,
            permanentLocationId = permanentLocationId,
            permanentLocationName = permanentLocationName,
            permanentLocationLat = permanentLocationLat,
            permanentLocationLng = permanentLocationLng,
            permanentLocationLocality = permanentLocationLocality,
            permanentLocationSubLocality = permanentLocationSubLocality,
            permanentLocationRoute = permanentLocationRoute,
            permanentLocationCity = permanentLocationCity,
            permanentLocationState = permanentLocationState,
            permanentLocationCountry = permanentLocationCountry,
            permanentLocationCountryCode = permanentLocationCountryCode,
            permanentLocationCompleteAddress = permanentLocationCompleteAddress,
        )
    }
}
