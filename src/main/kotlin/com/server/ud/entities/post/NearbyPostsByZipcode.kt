package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.MediaType
import com.server.common.utils.DateUtils
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.dto.AllCategoryV2Response
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

/**
 *
 * Write all the posts that are near to the given zipcode
 *
 * So, values would be like this:
 *
 * Zipcode of the Post -> 560037
 * Nearby Zipcodes -> 560037, 560025, 560001
 *
 * zipcode = 560037, originalZipcode = 560037
 * zipcode = 560025, originalZipcode = 560037
 * zipcode = 560001, originalZipcode = 560037
 *
 * and so on
 *
 * */

@Table("nearby_posts_by_zipcode")
data class NearbyPostsByZipcode (

    // Keeping a composite key to create a partition for
    // a location on daily basis
    // otherwise a single location can lead to skewed partition
    // when number of post from a single position increases
    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

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

    @Column("original_zipcode")
    var originalZipcode: String,

    @Column("location_id")
    var locationId: String? = null,

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
    var tags: String? = null, // List of HashTagList

    @Column
    var categories: String? = null, //  List of CategoryV2

)

@Table("nearby_posts_by_zipcode_tracker")
data class NearbyPostsByZipcodeTracker (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

//    // Modulo 10 to create 10 shards based on Post Id/user id so that there
//    // is a mechanism to retrieve it later on as well just by using the ids
//    // user id will give better cardinality
//    @PrimaryKeyColumn(name = "shard_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
//    var shardId: Int,

    @PrimaryKeyColumn(name = "post_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var postType: PostType,

    @PrimaryKeyColumn(name = "zipcode", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var zipcode: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "user_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @Column("original_zipcode")
    var originalZipcode: String,

    @Column("location_id")
    var locationId: String? = null,

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
    var tags: String? = null, // List of HashTagList

    @Column
    var categories: String? = null, //  List of CategoryV2

)


fun NearbyPostsByZipcodeTracker.toNearbyPostsByZipcode(): NearbyPostsByZipcode {
    this.apply {
        return NearbyPostsByZipcode(
            postId = this.postId,
            postType = this.postType,
            zipcode = this.zipcode,
            createdAt = this.createdAt,
            userId = this.userId,
            originalZipcode = this.originalZipcode,
            locationId = this.locationId,
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


fun NearbyPostsByZipcode.toNearbyPostsByZipcodeTracker(): NearbyPostsByZipcodeTracker {
    this.apply {
        return NearbyPostsByZipcodeTracker(
            postId = this.postId,
            postType = this.postType,
            zipcode = this.zipcode,
            createdAt = this.createdAt,
            userId = this.userId,
            originalZipcode = this.originalZipcode,
            locationId = this.locationId,
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

fun NearbyPostsByZipcode.toNearbyVideoPostsByZipcode(): NearbyVideoPostsByZipcode? {
    this.apply {
        val hasVideo = getMediaDetails().media.any { it.mediaType == MediaType.VIDEO }
        return if (hasVideo) {
            NearbyVideoPostsByZipcode(
                zipcode = zipcode,
                postType = postType,
                createdAt = createdAt,
                postId = postId,
                userId = userId,
                originalZipcode = originalZipcode,
                locationId = locationId,
                locationName = locationName,
                locationLat = locationLat,
                locationLng = locationLng,
                title = title,
                description = description,
                media = media,
                sourceMedia = sourceMedia,
                tags = tags,
                categories = categories,
                locality = locality,
                subLocality = subLocality,
                route = route,
                city = city,
                state = state,
                country = country,
                countryCode = countryCode,
                completeAddress = completeAddress,
            )
        } else {
            null
        }
    }
}

fun NearbyPostsByZipcode.getMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}

fun NearbyPostsByZipcode.getSourceMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(sourceMedia)
    }
}

fun NearbyPostsByZipcode.getHashTags(): AllHashTags {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(tags, AllHashTags::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            AllHashTags(emptySet())
        }
    }
}

fun NearbyPostsByZipcode.getCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            AllCategoryV2Response(emptyList())
        }
    }
}
