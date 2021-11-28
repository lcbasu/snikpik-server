package com.server.ud.entities.post

import com.server.common.dto.AllLabelsResponse
import com.server.common.dto.AllProfileTypeResponse
import com.server.dk.model.MediaDetailsV2
import com.server.ud.dto.AllCategoryV2Response
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags

data class GeoLoc (
    val lat: Double,
    val lng: Double
)

data class AlgoliaPost (
    val objectID: String,
    val postId: String,
    val createdAt: Long,
    val userId: String,
    val postType: PostType,
    val title: String? = null,
    val userHandle: String? = null,
    val userName: String? = null,
    val userMobile: String? = null,
    val userProfiles: AllProfileTypeResponse? = null,
    val description: String? = null,
    val media: MediaDetailsV2? = null,
    val tags: AllHashTags? = null,
    val categories: AllCategoryV2Response? = null,
    val locationId: String? = null,
    val googlePlaceId: String? = null,
    val zipcode: String? = null,
    val locationName: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val _geoloc: GeoLoc? = null,
    val labels: AllLabelsResponse? = null,
)
