package com.server.ud.entities.post

import com.server.common.utils.DateUtils
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

/**
 *
 * user_id -> Lokesh
 * following_user_id -> Amitabh Bachhan
 *
 * So whenever Amitabh Bachhan creates a post, we go ahead and add that post into posts_by_following
 * table for the all the followers so that they(user, Lokesh) can see the feed from people who are
 * being followed(Amitabh Bachhan) by that user (Lokesh)
 *
 *
 * */

@Table("posts_by_following")
class PostsByFollowing (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "following_user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var followingUserId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    var postType: PostType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "post_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var postId: String,

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
)

