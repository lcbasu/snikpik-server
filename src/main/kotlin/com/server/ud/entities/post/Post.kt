package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.ProfileType
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.enums.CategoryV2
import com.server.common.enums.MediaPresenceType
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagsList
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.time.Instant

@Table("posts")
data class Post (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var postType: PostType,

    @PrimaryKeyColumn(name = "media_presence_type", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var mediaPresenceType: MediaPresenceType,

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

    @Column("user_profile")
    var userProfile: ProfileType? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of HashTagsList

    @Column
    var categories: String? = null, //  List of CategoryV2

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
)

fun Post.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(media, MediaDetailsV2::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

fun Post.getGeoPointData(): GeoPoint? {
    this.apply {
        if (locationLat != null && locationLng != null) {
            return GeoPoint(locationLat,locationLng)
        }
        return null
    }
}

fun Post.getHashTags(): HashTagsList {
    this.apply {
        return try {
            return jacksonObjectMapper().readValue(tags, HashTagsList::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            HashTagsList(emptyList())
        }
    }
}

fun Post.getCategories(): Set<CategoryV2> {
    this.apply {
        return try {
            val categoryIds = categories?.trim()?.split(",") ?: emptySet()
            return categoryIds.map {
                CategoryV2.valueOf(it)
            }.toSet()
        } catch (e: Exception) {
            e.printStackTrace()
            emptySet()
        }
    }
}

