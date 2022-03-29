package com.server.ud.entities.post

import com.server.common.dto.AllCategoryV2Response
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.model.MediaDetailsV2
import com.server.common.model.MediaTypeDetail
import com.server.common.utils.DateUtils
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.persistence.Id

@Document("posts")
data class PostMongoDB (

    val schemaVersion: Int = 2,

    @Id
    var postId: String,

    var createdAt: Date = DateUtils.dateNow(),

    var userId: String,

    var postType: PostType,

    var mediaTypeDetail: MediaTypeDetail,

    var labels: String? = null,

    var title: String? = null,

    var userHandle: String? = null,

    var userName: String? = null,

    var userMobile: String? = null,

    var userCountryCode: String? = null,

    var userProfiles: AllProfileTypeResponse? = null,

    var description: String? = null,

    var media: MediaDetailsV2? = null, // MediaDetailsV2

    var sourceMedia: MediaDetailsV2? = null, // MediaDetailsV2

    var tags: AllHashTags? = null, // List of AllHashTags

    var categories: AllCategoryV2Response? = null, //  List of AllCategoryV2Response

    var geoPoint: MongoGeoPoint? = null,

    var locationId: String? = null,

    var googlePlaceId: String? = null,

    var zipcode: String? = null,

    val locationName: String? = null,

    val locationLat: Double? = null,

    val locationLng: Double? = null,

    val locality: String? = null,

    val subLocality: String? = null,

    val route: String? = null,

    val city: String? = null,

    val state: String? = null,

    val country: String? = null,

    val countryCode: String? = null,

    val completeAddress: String? = null,
)

data class MongoGeoPoint (
    val type: String = "Point",
    val coordinates: List<Double>
)

fun getMongoGeoPoint(lat: Double?, lng: Double?): MongoGeoPoint? {
    return if (lat != null && lng != null) {
        MongoGeoPoint(coordinates = listOf(lng, lat))
    } else null
}
