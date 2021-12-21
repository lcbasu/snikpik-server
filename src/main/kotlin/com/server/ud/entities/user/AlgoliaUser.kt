package com.server.ud.entities.user

import com.server.common.dto.AllProfileTypeResponse
import com.server.common.utils.DateUtils
import com.server.ud.dto.AllCategoryV2Response
import com.server.ud.entities.post.GeoLoc

data class AlgoliaUser (
    val objectID: String,
    val userId: String,
    var createdAt: Long,
    val absoluteMobile: String? = null,
    val countryCode: String? = null,
    val handle: String? = null,
    val email: String? = null,
    val dp: String? = null,
    val uid: String? = "",
    val anonymous: Boolean = false,
    val verified: Boolean = false,
    val profiles: AllProfileTypeResponse = AllProfileTypeResponse(emptyList()),
    val fullName: String? = "",

    // Permanent location info
    val _geoloc: GeoLoc? = null,
    val permanentLocationZipcode: String? = null,

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
            dp = dp,
            uid = uid,
            anonymous = anonymous,
            verified = verified,
            profiles = getProfiles(),
            fullName = fullName,
            _geoloc = if (permanentLocationLat != null && permanentLocationLng != null) GeoLoc(
                lat = permanentLocationLat,
                lng = permanentLocationLng
            ) else null,
            permanentLocationZipcode = permanentLocationZipcode,
            preferredCategories = getPreferredCategories(),
        )
    }
}
