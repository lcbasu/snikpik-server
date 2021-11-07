package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProfileType
import com.server.dk.model.MediaDetailsV2
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getProfiles
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2HandleRequest (
    val userId: String,
    val newHandle: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2DPRequest (
    val userId: String,
    val dp: MediaDetailsV2,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2ProfilesRequest (
    val userId: String,
    val profiles: Set<ProfileType>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2NameRequest (
    val userId: String,
    val newName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2LocationRequest (
    val userId: String,
    val lat: Double,
    val lng: Double,
    var zipcode: String,
    val name: String?,
    var googlePlaceId: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserV2Response(
    var userId: String,
    val fullName: String?,
    val uid: String?,
    val anonymous: Boolean?,
    val absoluteMobile: String?,
    val countryCode: String?,
    val notificationToken: String?,
    val notificationTokenProvider: NotificationTokenProvider?,
    var createdAt: Instant,
    var handle: String?,
    var dp: String?, // MediaDetailsV2
    var verified: Boolean?,
    var profiles: Set<ProfileType>,
    var userLastLocationZipcode: String?,
    var userLastGooglePlaceId: String?,
    var userLastLocationId: String?,
    val userLastLocationName: String?,
    val userLastLocationLat: Double?,
    val userLastLocationLng: Double?,
)

fun UserV2.toSavedUserV2Response(): SavedUserV2Response {
    this.apply {
        return SavedUserV2Response(
            userId = userId,
            fullName = fullName,
            uid = uid,
            anonymous = anonymous,
            absoluteMobile = absoluteMobile,
            countryCode = countryCode,
            notificationToken = notificationToken,
            notificationTokenProvider = notificationTokenProvider,
            createdAt = createdAt,
            handle = handle,
            dp = dp,
            verified = verified,
            profiles = getProfiles(),
            userLastLocationZipcode = userLastLocationZipcode,
            userLastGooglePlaceId = userLastGooglePlaceId,
            userLastLocationId = userLastLocationId,
            userLastLocationName = userLastLocationName,
            userLastLocationLat = userLastLocationLat,
            userLastLocationLng = userLastLocationLng,
        )
    }
}
