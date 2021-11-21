package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.common.enums.MediaPresenceType
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagsList
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
class NearbyPostsByZipcode (

    // Keeping a composite key to create a partition for
    // a location on daily basis
    // otherwise a single location can lead to skewed partition
    // when number of post from a single position increases
    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

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

    @PrimaryKeyColumn(name = "media_presence_type", ordinal = 6, type = PrimaryKeyType.CLUSTERED)
    var mediaPresenceType: MediaPresenceType,

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
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of HashTagList

    @Column
    var categories: String? = null, //  List of CategoryV2

)

fun NearbyPostsByZipcode.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(media, MediaDetailsV2::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

fun NearbyPostsByZipcode.getHashTags(): HashTagsList {
    this.apply {
        return try {
            return jacksonObjectMapper().readValue(tags, HashTagsList::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            HashTagsList(emptyList())
        }
    }
}
