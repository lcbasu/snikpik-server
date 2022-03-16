package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.dto.AllLabelsResponse
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.utils.DateUtils
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.dto.AllCategoryV2Response
import com.server.ud.enums.PostType
import com.server.ud.enums.PostUpdateType
import com.server.ud.model.AllHashTags
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

data class PostUpdate (
    val updateTypes: List<PostUpdateType>,
    val oldPost: Post,
    val newPost: Post? = null,
)

fun Post.getLabels(): AllLabelsResponse {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(labels, AllLabelsResponse::class.java)
        } catch (e: Exception) {
            AllLabelsResponse(emptySet())
        }
    }
}

fun Post.getCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            AllCategoryV2Response(emptyList())
        }
    }
}

fun Post.getUserProfiles(): AllProfileTypeResponse {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(userProfiles, AllProfileTypeResponse::class.java)
        } catch (e: Exception) {
            AllProfileTypeResponse(emptyList())
        }
    }
}

fun Post.getMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}

fun Post.getSourceMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(sourceMedia)
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

fun Post.getHashTags(): AllHashTags {
    this.apply {
        return try {
            return jacksonObjectMapper().readValue(tags, AllHashTags::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            AllHashTags(emptySet())
        }
    }
}

fun Post.toAlgoliaPostAutoSuggest(): List<AlgoliaPostAutoSuggest> {
    this.apply {
        val result = mutableListOf<AlgoliaPostAutoSuggest>()

        // Only take first 50 characters of title
        title?.take(50)?.toAlgoliaPostAutoSuggest()?.let {
            result.add(it)
        }

        // Only take first 50 characters of description
        description?.take(50)?.toAlgoliaPostAutoSuggest()?.let {
            result.add(it)
        }

        userName?.toAlgoliaPostAutoSuggest()?.let {
            result.add(it)
        }

        getUserProfiles().profileTypes.map {
            result.add(it.displayName.toAlgoliaPostAutoSuggest())
        }

        getHashTags().tags.map {
            result.add(it.toAlgoliaPostAutoSuggest())
        }

        getCategories().categories.map {
            result.add(it.displayName.toAlgoliaPostAutoSuggest())
        }

        locationName?.let {
            result.add(it.toAlgoliaPostAutoSuggest())
        }

        zipcode?.let {
            result.add(it.toAlgoliaPostAutoSuggest())
        }

        getLabels().labels.map {
            result.add(it.toAlgoliaPostAutoSuggest())
        }

        return result
    }
}
