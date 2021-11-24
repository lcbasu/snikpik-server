package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.dto.toProfileTypeResponse
import com.server.common.enums.ProfileType
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.dto.toCategoryV2Response
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagsList
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.time.Instant

@Table("posts")
data class Post (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
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

fun Post.getUserProfiles(): Set<ProfileType> {
    this.apply {
        return try {
            if (userProfiles.isNullOrBlank()) {
                return emptySet()
            }
            val profileIds = userProfiles?.trim()?.split(",") ?: emptySet()
            return profileIds.map {
                ProfileType.valueOf(it)
            }.toSet()
        } catch (e: Exception) {
            e.printStackTrace()
            emptySet()
        }
    }
}

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


fun Post.toAlgoliaPost(): AlgoliaPost {
    this.apply {
        return AlgoliaPost(
            objectID = postId,
            createdAt = DateUtils.getEpoch(createdAt),
            userId = userId,
            postType = postType,
            title = title,
            userHandle = userHandle,
            userName = userName,
            userMobile = userMobile,
            userProfiles = getUserProfiles().map { it.toProfileTypeResponse() }.toSet(),
            description = description,
            media = getMediaDetails(),
            tags = getHashTags(),
            categories = getCategories().map { it.toCategoryV2Response() },
            locationId = locationId,
            googlePlaceId = googlePlaceId,
            zipcode = zipcode,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
        )
    }
}
