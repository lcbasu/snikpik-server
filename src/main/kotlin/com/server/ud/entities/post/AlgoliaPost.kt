package com.server.ud.entities.post

import com.server.common.dto.AllLabelsResponse
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.utils.CommonUtils
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.common.dto.AllCategoryV2Response
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
    val sourceMedia: MediaDetailsV2? = null,
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

data class AlgoliaPostAutoSuggest (
    val objectID: String,
    val nb_words: Int,
    val popularity: Int,
    val query: String,
)

fun String.toAlgoliaPostAutoSuggest(): AlgoliaPostAutoSuggest {
    this.apply {
        return AlgoliaPostAutoSuggest(
            objectID = CommonUtils.getLowercaseStringWithOnlyCharOrDigit(this),
            nb_words = 1,
            popularity = 1,
            query = this,
        )
    }
}

fun Post.toAlgoliaPost(): AlgoliaPost {
    this.apply {
        return AlgoliaPost(
            objectID = postId,
            postId = postId,
            createdAt = DateUtils.getEpoch(createdAt),
            userId = userId,
            postType = postType,
            title = title,
            userHandle = userHandle,
            userName = userName,
            userMobile = userMobile,
            userProfiles = getUserProfiles(),
            description = description,
            media = getMediaDetails(),
            sourceMedia = getSourceMediaDetails(),
            tags = getHashTags(),
            categories = getCategories(),
            locationId = locationId,
            googlePlaceId = googlePlaceId,
            zipcode = zipcode,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
            _geoloc = if (locationLat != null && locationLng != null) GeoLoc(
                lat = locationLat,
                lng = locationLng
            ) else null,
            labels = getLabels()
        )
    }
}
