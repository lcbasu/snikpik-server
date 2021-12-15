package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.utils.DateUtils
import com.server.common.model.MediaDetailsV2
import com.server.ud.dto.AllCategoryV2Response
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.time.Instant

@Table("posts_by_category")
class PostsByCategory (

    @PrimaryKeyColumn(name = "category_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var categoryId: CategoryV2,

    @PrimaryKeyColumn(name = "post_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var postType: PostType,

    @PrimaryKeyColumn(name = "for_date", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "post_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var postId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 5, type = PrimaryKeyType.CLUSTERED)
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
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of AllHashTags

    @Column
    var categories: String? = null, //  List of AllCategoryV2

)

fun PostsByCategory.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(media, MediaDetailsV2::class.java)
        } catch (e: Exception) {
            MediaDetailsV2(emptyList())
        }
    }
}

fun PostsByCategory.getGeoPointData(): GeoPoint? {
    this.apply {
        if (locationLat != null && locationLng != null) {
            return GeoPoint(locationLat,locationLng)
        }
        return null
    }
}

fun PostsByCategory.getHashTags(): AllHashTags {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(tags, AllHashTags::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            AllHashTags(emptySet())
        }
    }
}

fun PostsByCategory.getCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            AllCategoryV2Response(emptyList())
        }
    }
}


