package com.server.ud.entities.post

import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("posts_by_hash_tag")
data class PostsByHashTag (

    @PrimaryKeyColumn(name = "hash_tag_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var hashTagId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var postType: PostType,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "post_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var postId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

//    @Column("hash_tag_display_name")
//    var hashTagDisplayName: String,

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
    var categories: String? = null, //  List of AllCategoryV2Response

)

@Table("posts_by_hash_tag_tracker")
data class PostsByHashTagTracker (


    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,


    @PrimaryKeyColumn(name = "hash_tag_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var hashTagId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var postType: PostType,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "user_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

//    @Column("hash_tag_display_name")
//    var hashTagDisplayName: String,

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
    var categories: String? = null, //  List of AllCategoryV2Response

)

fun PostsByHashTag.toPostsByHashTagTracker(): PostsByHashTagTracker {
    this.apply {
        return PostsByHashTagTracker(
            postId = this.postId,
            hashTagId = this.hashTagId,
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

fun PostsByHashTagTracker.toPostsByHashTag(): PostsByHashTag {
    this.apply {
        return PostsByHashTag(
            postId = this.postId,
            hashTagId = this.hashTagId,
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
