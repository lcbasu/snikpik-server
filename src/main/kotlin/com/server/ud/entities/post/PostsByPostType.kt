package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.dto.AllCategoryV2Response
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("posts_by_post_type")
class PostsByPostType (

    @PrimaryKeyColumn(name = "post_type", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postType: PostType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "post_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var postId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @Column("location_id")
    var locationId: String? = null,

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

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column("source_media")
    var sourceMedia: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of AllHashTags

    @Column
    var categories: String? = null, //  List of AllCategoryV2

)

@Table("posts_by_post_type_tracker")
class PostsByPostTypeTracker (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var postType: PostType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @Column("location_id")
    var locationId: String? = null,

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

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column("source_media")
    var sourceMedia: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of AllHashTags

    @Column
    var categories: String? = null, //  List of AllCategoryV2

)


fun PostsByPostTypeTracker.toPostsByPostType(): PostsByPostType {
    this.apply {
        return PostsByPostType(
            postId = this.postId,
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

fun PostsByPostType.toPostsByPostTypeTracker(): PostsByPostTypeTracker {
    this.apply {
        return PostsByPostTypeTracker(
            postId = this.postId,
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



fun PostsByPostType.getMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}

fun PostsByPostType.getSourceMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(sourceMedia)
    }
}

fun PostsByPostType.getHashTags(): AllHashTags {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(tags, AllHashTags::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            AllHashTags(emptySet())
        }
    }
}

fun PostsByPostType.getCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            AllCategoryV2Response(emptyList())
        }
    }
}
