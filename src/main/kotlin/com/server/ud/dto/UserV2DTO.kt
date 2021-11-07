package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.enums.NotificationTokenProvider
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.enums.UserProfession
import java.time.Instant

data class UserV2DetailResponse (
    val userId: String,
    val handle: String?,
    val name: String?,
    val dp: MediaDetailsV2?,
    val verified: Boolean?,
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserResponse(
    val serverId: String,
    val name: String,
    val uid: String,
    val anonymous: Boolean,
    val absoluteMobile: String,
    val countryCode: String,
    val defaultAddressId: String,
    val notificationToken: String,
    val notificationTokenProvider: NotificationTokenProvider,
    var userId: String,
    var createdAt: Instant = DateUtils.getInstantNow(),
    var handle: String? = null,
    var dp: String? = null, // MediaDetailsV2
    var verified: Boolean = false,
    var profession: UserProfession? = null,
    var fullName: String? = "",
    var userLastLocationZipcode: String? = null,
    var userLastGooglePlaceId: String? = null,
    var userLastLocationId: String? = null,
    val userLastLocationName: String? = null,
    val userLastLocationLat: Double? = null,
    val userLastLocationLng: Double? = null,
)
