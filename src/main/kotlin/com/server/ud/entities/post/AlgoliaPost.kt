package com.server.ud.entities.post

import com.server.common.dto.ProfileTypeResponse
import com.server.dk.model.MediaDetailsV2
import com.server.ud.dto.CategoryV2Response
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagsList

data class GeoLoc (
    val lat: Double,
    val lng: Double
)

data class AlgoliaPost (
    var objectID: String,
    var createdAt: Long,
    var userId: String,
    var postType: PostType,
    var title: String? = null,
    var userHandle: String? = null,
    var userName: String? = null,
    var userMobile: String? = null,
    var userProfiles: Set<ProfileTypeResponse> = emptySet(),
    var description: String? = null,
    var media: MediaDetailsV2? = null,
    var tags: HashTagsList? = null,
    var categories: List<CategoryV2Response>? = null,
    var locationId: String? = null,
    var googlePlaceId: String? = null,
    var zipcode: String? = null,
    val locationName: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val _geoloc: GeoLoc? = null,
)
