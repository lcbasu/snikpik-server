package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.common.dto.AllCategoryV2Response
import com.server.ud.dto.PostsByUserPostDetail
import com.server.ud.dto.SavedPostResponse
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("posts_by_user")
data class PostsByUser (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var postType: PostType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "post_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var postId: String,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column("source_media")
    var sourceMedia: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of HashTagList

    @Column
    var categories: String? = null, //  List of CategoryV2

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
)

fun PostsByUser.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}

fun PostsByUser.getSourceMediaDetails(): MediaDetailsV2? {
    this.apply {
        return getMediaDetailsFromJsonString(sourceMedia)
    }
}

fun PostsByUser.getHashTags(): AllHashTags {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(tags, AllHashTags::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            AllHashTags(emptySet())
        }
    }
}

fun PostsByUser.getCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            AllCategoryV2Response(emptyList())
        }
    }
}

fun PostsByUser.toPostsByUserPostDetail(): PostsByUserPostDetail {
    this.apply {
        return PostsByUserPostDetail(
            postId = postId,
            userId = userId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(createdAt),
            description = description,
        )
    }
}

fun PostsByUser.toSavedPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            postType = postType,
            userId = userId,
            locationId = locationId,
            zipcode = zipcode,
            googlePlaceId = null,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            city = city,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
            createdAt = DateUtils.getEpoch(createdAt),
            title = title,
            description = description,
            tags = getHashTags(),
            categories = getCategories(),
            mediaDetails = getMediaDetails(),
            media = getMediaDetails(),
            sourceMediaDetails = getSourceMediaDetails(),
        )
    }
}
