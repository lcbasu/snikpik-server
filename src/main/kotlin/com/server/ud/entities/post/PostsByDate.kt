package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.dto.AllCategoryV2Response
import com.server.common.dto.AllLabelsResponse
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.time.Instant

@Table("posts_by_date")
data class PostsByDate (

    @PrimaryKeyColumn(name = "for_date", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var forDate: String,

    @PrimaryKeyColumn(name = "post_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var postId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("user_id")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var userId: String,

    @Column("post_type")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var postType: PostType,

    @Column("labels")
    var labels: String? = null,

    @Column
    var title: String? = null,

    @Column("user_handle")
    var userHandle: String? = null,

    @Column("user_name")
    var userName: String? = null,

    @Column("user_mobile")
    var userMobile: String? = null,

    @Column("user_country_code")
    var userCountryCode: String? = null,

    @Column("user_profiles")
    var userProfiles: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column("source_media")
    var sourceMedia: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of AllHashTags

    @Column
    var categories: String? = null, //  List of AllCategoryV2Response

    @Column("location_id")
    var locationId: String? = null,

    @Column("google_place_id")
    var googlePlaceId: String? = null,

    @Column("zipcode")
    var zipcode: String? = null,

    @Column("location_name")
    val locationName: String? = null,

    @Column("location_lat")
    val locationLat: Double? = null,

    @Column("location_lng")
    val locationLng: Double? = null,

    @Column
    val locality: String? = null,

    @Column("sub_locality")
    val subLocality: String? = null,

    @Column
    val route: String? = null,

    @Column
    val city: String? = null,

    @Column
    val state: String? = null,

    @Column
    val country: String? = null,

    @Column("country_code")
    val countryCode: String? = null,

    @Column("complete_address")
    val completeAddress: String? = null,
)


@Table("posts_by_date_tracker")
data class PostsByDateTracker (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var forDate: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("user_id")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var userId: String,

    @Column("post_type")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var postType: PostType,

    @Column("labels")
    var labels: String? = null,

    @Column
    var title: String? = null,

    @Column("user_handle")
    var userHandle: String? = null,

    @Column("user_name")
    var userName: String? = null,

    @Column("user_mobile")
    var userMobile: String? = null,

    @Column("user_country_code")
    var userCountryCode: String? = null,

    @Column("user_profiles")
    var userProfiles: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column("source_media")
    var sourceMedia: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of AllHashTags

    @Column
    var categories: String? = null, //  List of AllCategoryV2Response

    @Column("location_id")
    var locationId: String? = null,

    @Column("google_place_id")
    var googlePlaceId: String? = null,

    @Column("zipcode")
    var zipcode: String? = null,

    @Column("location_name")
    val locationName: String? = null,

    @Column("location_lat")
    val locationLat: Double? = null,

    @Column("location_lng")
    val locationLng: Double? = null,

    @Column
    val locality: String? = null,

    @Column("sub_locality")
    val subLocality: String? = null,

    @Column
    val route: String? = null,

    @Column
    val city: String? = null,

    @Column
    val state: String? = null,

    @Column
    val country: String? = null,

    @Column("country_code")
    val countryCode: String? = null,

    @Column("complete_address")
    val completeAddress: String? = null,
)


fun PostsByDate.toPostsByDateTracker(): PostsByDateTracker {
    this.apply {
        return PostsByDateTracker(
            postId = this.postId,
            forDate = this.forDate,
            postType = this.postType,
            createdAt = this.createdAt,
            userId = this.userId,
            locationId = this.locationId,
            zipcode = this.zipcode,
            locationName = this.locationName,
            locationLat = this.locationLat,
            locationLng = this.locationLng,
            locality = this.locality,
            subLocality = this.subLocality,
            route = this.route,
            city = this.city,
            state = this.state,
            country = this.country,
            countryCode = this.countryCode,
            completeAddress = this.completeAddress,
            title = this.title,
            description = this.description,
            media = this.media,
            sourceMedia = this.sourceMedia,
            tags = this.tags,
            categories = this.categories
        )
    }
}

fun PostsByDateTracker.toPostsByDate(): PostsByDate {
    this.apply {
        return PostsByDate(
            postId = this.postId,
            forDate = this.forDate,
            postType = this.postType,
            createdAt = this.createdAt,
            userId = this.userId,
            locationId = this.locationId,
            zipcode = this.zipcode,
            locationName = this.locationName,
            locationLat = this.locationLat,
            locationLng = this.locationLng,
            locality = this.locality,
            subLocality = this.subLocality,
            route = this.route,
            city = this.city,
            state = this.state,
            country = this.country,
            countryCode = this.countryCode,
            completeAddress = this.completeAddress,
            title = this.title,
            description = this.description,
            media = this.media,
            sourceMedia = this.sourceMedia,
            tags = this.tags,
            categories = this.categories
        )
    }
}

fun PostsByDate.getLabels(): AllLabelsResponse {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(labels, AllLabelsResponse::class.java)
        } catch (e: Exception) {
            AllLabelsResponse(emptySet())
        }
    }
}

fun PostsByDate.getCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            AllCategoryV2Response(emptyList())
        }
    }
}

fun PostsByDate.getUserProfiles(): AllProfileTypeResponse {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(userProfiles, AllProfileTypeResponse::class.java)
        } catch (e: Exception) {
            AllProfileTypeResponse(emptyList())
        }
    }
}

fun PostsByDate.getMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}

fun PostsByDate.getSourceMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(sourceMedia)
    }
}

fun PostsByDate.getGeoPointData(): GeoPoint? {
    this.apply {
        if (locationLat != null && locationLng != null) {
            return GeoPoint(locationLat,locationLng)
        }
        return null
    }
}

fun PostsByDate.getHashTags(): AllHashTags {
    this.apply {
        return try {
            return jacksonObjectMapper().readValue(tags, AllHashTags::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            AllHashTags(emptySet())
        }
    }
}

