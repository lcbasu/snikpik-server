package com.server.ud.entities.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.ProfilePageUserDetailsResponse
import com.server.common.dto.SavedUserV2Response
import com.server.common.dto.UserV2PublicMiniDataResponse
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProfileCategory
import com.server.common.utils.DateUtils
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.dto.AllCategoryV2Response
import com.server.ud.dto.SaveLocationRequest
import com.server.ud.enums.LocationFor
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Table("users")
data class UserV2 (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val userId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("absolute_mobile")
    val absoluteMobile: String? = null,

    @Column("country_code")
    val countryCode: String? = null,

    @Column("handle")
    val handle: String? = null,

    @Column
    val email: String? = null,

    @Column
    val dp: String? = null, // MediaDetailsV2

    @Column("cover_image")
    val coverImage: String? = null, // MediaDetailsV2

    @Column
    val uid: String? = "",

    @Column
    val anonymous: Boolean = false,

    @Column
    val verified: Boolean = false,

    @Column
    val profiles: String? = null, // Set of AllProfileTypeResponse as String

    @Column("full_name")
    val fullName: String? = "",

    @Column("notification_token")
    val notificationToken: String? = "",

    @Column("notification_token_provider")
    @Enumerated(EnumType.STRING)
    val notificationTokenProvider: NotificationTokenProvider? = NotificationTokenProvider.FIREBASE,

    /**
     *
     *  This location keeps changing whenever thes things change
     *
     *  1. IP Address
     *  2. Location where the user is doing search
     *
     * */
    @Column("current_zipcode")
    val currentLocationZipcode: String? = null,

    @Column("current_google_place_id")
    val currentGooglePlaceId: String? = null,

    @Column("current_location_id")
    val currentLocationId: String? = null,

    @Column("current_location_name")
    val currentLocationName: String? = null,

    @Column("current_location_lat")
    val currentLocationLat: Double? = null,

    @Column("current_location_lng")
    val currentLocationLng: Double? = null,

    @Column("current_location_locality")
    val currentLocationLocality: String? = null,

    @Column("current_location_sub_locality")
    val currentLocationSubLocality: String? = null,

    @Column("current_location_route")
    val currentLocationRoute: String? = null,

    @Column("current_location_city")
    val currentLocationCity: String? = null,

    @Column("current_location_state")
    val currentLocationState: String? = null,

    @Column("current_location_country")
    val currentLocationCountry: String? = null,

    @Column("current_location_country_code")
    val currentLocationCountryCode: String? = null,

    @Column("current_location_complete_address")
    val currentLocationCompleteAddress: String? = null,

    /**
     * Permanent location.
     *
     * This location is never automatically set.
     * But it is set when user specifies the location of the work or location of their home
     *
     * */

    @Column("permanent_zipcode")
    val permanentLocationZipcode: String? = null,

    @Column("permanent_google_place_id")
    val permanentGooglePlaceId: String? = null,

    @Column("permanent_location_id")
    val permanentLocationId: String? = null,

    @Column("permanent_location_name")
    val permanentLocationName: String? = null,

    @Column("permanent_location_lat")
    val permanentLocationLat: Double? = null,

    @Column("permanent_location_lng")
    val permanentLocationLng: Double? = null,

    @Column("permanent_location_locality")
    val permanentLocationLocality: String? = null,

    @Column("permanent_location_sub_locality")
    val permanentLocationSubLocality: String? = null,

    @Column("permanent_location_route")
    val permanentLocationRoute: String? = null,

    @Column("permanent_location_city")
    val permanentLocationCity: String? = null,

    @Column("permanent_location_state")
    val permanentLocationState: String? = null,

    @Column("permanent_location_country")
    val permanentLocationCountry: String? = null,

    @Column("permanent_location_country_code")
    val permanentLocationCountryCode: String? = null,

    @Column("permanent_location_complete_address")
    val permanentLocationCompleteAddress: String? = null,

    @Column("preferred_categories")
    val preferredCategories: String? = null,  //  List of AllCategoryV2Response

)

fun UserV2.getPreferredCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(preferredCategories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            AllCategoryV2Response(emptyList())
        }
    }
}

fun UserV2.getMediaDetailsForDP(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(dp)
    }
}

fun UserV2.getMediaDetailsForCoverImage(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(coverImage)
    }
}

fun UserV2.getProfiles(): AllProfileTypeResponse {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(profiles, AllProfileTypeResponse::class.java)
        } catch (e: Exception) {
//            e.printStackTrace()
            AllProfileTypeResponse(emptyList())
        }
    }
}

fun UserV2.getSaveLocationRequestFromCurrentLocation(): SaveLocationRequest? {
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

fun UserV2.getSaveLocationRequestFromPermanentLocation(): SaveLocationRequest? {
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


fun UserV2.toSavedUserV2Response(): SavedUserV2Response {
    this.apply {
        return SavedUserV2Response(
            userId = userId,
            fullName = fullName,
            uid = uid,
            anonymous = anonymous,
            absoluteMobile = absoluteMobile,
            email = email,
            countryCode = countryCode,
            notificationToken = notificationToken,
            notificationTokenProvider = notificationTokenProvider,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            dp = getMediaDetailsForDP(),
            coverImage = getMediaDetailsForCoverImage(),
            verified = verified,
            profiles = getProfiles(),
            preferredCategories = getPreferredCategories(),

            currentLocationId = currentLocationId,
            currentLocationLat = currentLocationLat,
            currentLocationLng = currentLocationLng,
            currentLocationZipcode = currentLocationZipcode,
            currentLocationName = currentLocationName,
            currentGooglePlaceId = currentGooglePlaceId,
            currentLocationLocality = currentLocationLocality,
            currentLocationSubLocality = currentLocationSubLocality,
            currentLocationRoute = currentLocationRoute,
            currentLocationCity = currentLocationCity,
            currentLocationState = currentLocationState,
            currentLocationCountry = currentLocationCountry,
            currentLocationCountryCode = currentLocationCountryCode,
            currentLocationCompleteAddress = currentLocationCompleteAddress,

            permanentLocationId = permanentLocationId,
            permanentLocationLat = permanentLocationLat,
            permanentLocationLng = permanentLocationLng,
            permanentLocationZipcode = permanentLocationZipcode,
            permanentLocationName = permanentLocationName,
            permanentGooglePlaceId = permanentGooglePlaceId,
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

fun UserV2.toProfilePageUserDetailsResponse(): ProfilePageUserDetailsResponse {
    this.apply {
        val profileToShow = getProfiles().profileTypes.firstOrNull()
        return ProfilePageUserDetailsResponse(
            userId = userId,
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
            userCurrentLocationName = currentLocationName,
            userCurrentLocationZipcode = currentLocationZipcode,
            userPermanentLocationName = permanentLocationName,
            userPermanentLocationZipcode = permanentLocationZipcode,

            currentLocationId = currentLocationId,
            currentLocationLat = currentLocationLat,
            currentLocationLng = currentLocationLng,
            currentLocationZipcode = currentLocationZipcode,
            currentLocationName = currentLocationName,
            currentGooglePlaceId = currentGooglePlaceId,
            currentLocationLocality = currentLocationLocality,
            currentLocationSubLocality = currentLocationSubLocality,
            currentLocationRoute = currentLocationRoute,
            currentLocationCity = currentLocationCity,
            currentLocationState = currentLocationState,
            currentLocationCountry = currentLocationCountry,
            currentLocationCountryCode = currentLocationCountryCode,
            currentLocationCompleteAddress = currentLocationCompleteAddress,

            permanentLocationId = permanentLocationId,
            permanentLocationLat = permanentLocationLat,
            permanentLocationLng = permanentLocationLng,
            permanentLocationZipcode = permanentLocationZipcode,
            permanentLocationName = permanentLocationName,
            permanentGooglePlaceId = permanentGooglePlaceId,
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

fun UserV2.toUserV2PublicMiniDataResponse(): UserV2PublicMiniDataResponse {
    this.apply {
        val profileToShow = getProfiles().profileTypes.firstOrNull()
        return UserV2PublicMiniDataResponse(
            userId = userId,
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
