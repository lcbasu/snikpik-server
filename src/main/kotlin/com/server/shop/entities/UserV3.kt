package com.server.shop.entities

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.entities.Auditable
import com.server.common.enums.NotificationTokenProvider
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.dto.AllCategoryV2Response
import com.server.common.dto.UserV2PublicMiniDataResponse
import com.server.common.enums.ProfileCategory
import com.server.common.utils.DateUtils
import com.server.ud.dto.SaveLocationRequest
import com.server.ud.enums.LocationFor
import javax.persistence.*

@Entity(name = "user_v3")
class UserV3 : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    var absoluteMobile: String? = null
    var countryCode: String? = null

    var handle: String? = null
    var email: String? = null
    var dp: String? = null

    var coverImage: String? = null
    var uid: String = ""
    var anonymous: Boolean = false
    var verified: Boolean = false
    var profiles: String? = null

    var fullName: String = ""

    var notificationToken: String = ""

    @Enumerated(EnumType.STRING)
    var notificationTokenProvider: NotificationTokenProvider? = NotificationTokenProvider.FIREBASE

    var currentLocationZipcode: String? = null
    var currentGooglePlaceId: String? = null
    var currentLocationId: String? = null
    var currentLocationName: String? = null
    var currentLocationLat: Double? = null
    var currentLocationLng: Double? = null
    var currentLocationLocality: String? = null
    var currentLocationSubLocality: String? = null
    var currentLocationRoute: String? = null
    var currentLocationCity: String? = null
    var currentLocationState: String? = null
    var currentLocationCountry: String? = null
    var currentLocationCountryCode: String? = null
    var currentLocationCompleteAddress: String? = null


    var permanentLocationZipcode: String? = null
    var permanentGooglePlaceId: String? = null
    var permanentLocationId: String? = null
    var permanentLocationName: String? = null
    var permanentLocationLat: Double? = null
    var permanentLocationLng: Double? = null
    var permanentLocationLocality: String? = null
    var permanentLocationSubLocality: String? = null
    var permanentLocationRoute: String? = null
    var permanentLocationCity: String? = null
    var permanentLocationState: String? = null
    var permanentLocationCountry: String? = null
    var permanentLocationCountryCode: String? = null
    var permanentLocationCompleteAddress: String? = null

    var preferredCategories: String? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "default_address_id")
    var defaultAddress: AddressV3? = null;
}

fun UserV3.getPreferredCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(preferredCategories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            AllCategoryV2Response(emptyList())
        }
    }
}

fun UserV3.getMediaDetailsForDP(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(dp)
    }
}

fun UserV3.getMediaDetailsForCoverImage(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(coverImage)
    }
}

fun UserV3.getProfiles(): AllProfileTypeResponse {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(profiles, AllProfileTypeResponse::class.java)
        } catch (e: Exception) {
//            e.printStackTrace()
            AllProfileTypeResponse(emptyList())
        }
    }
}

fun UserV3.getSaveLocationRequestFromCurrentLocation(): SaveLocationRequest? {
    this.apply {
        return if (currentLocationId != null) {
            SaveLocationRequest(
                locationFor = LocationFor.USER,
                zipcode = currentLocationZipcode,
                googlePlaceId = currentGooglePlaceId,
                name = currentLocationName,
                lat = currentLocationLat,
                lng = currentLocationLng,
            )
        } else {
            null
        }
    }
}

fun UserV3.getSaveLocationRequestFromPermanentLocation(): SaveLocationRequest? {
    this.apply {
        return if (permanentLocationId != null) {
            SaveLocationRequest(
                locationFor = LocationFor.USER,
                zipcode = permanentLocationZipcode,
                googlePlaceId = permanentGooglePlaceId,
                name = permanentLocationName,
                lat = permanentLocationLat,
                lng = permanentLocationLng,
            )
        } else {
            null
        }
    }
}

fun UserV3.toUserV2PublicMiniDataResponse(): UserV2PublicMiniDataResponse {
    this.apply {
        val profileToShow = getProfiles().profileTypes.firstOrNull()
        return UserV2PublicMiniDataResponse(
            userId = id,
            fullName = fullName,
            uid = uid,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            email = if (profileToShow?.category !== ProfileCategory.OWNER) email else "",
            absoluteMobile = if (profileToShow?.category !== ProfileCategory.OWNER) absoluteMobile else "",
            dp = getMediaDetailsForDP(),
            coverImage = getMediaDetailsForCoverImage(),
            verified = verified,
            profileToShow = profileToShow,
            allProfileTypes = getProfiles(),

            clId = currentLocationId,
            clZipcode = currentLocationZipcode,
            clName = currentLocationName,
            clCity = currentLocationCity,

            plId = permanentLocationId,
            plZipcode = permanentLocationZipcode,
            plName = permanentLocationName,
            plCity = permanentLocationCity,
        )
    }
}
