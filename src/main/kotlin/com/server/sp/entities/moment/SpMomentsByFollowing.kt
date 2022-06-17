package com.server.sp.entities.moment

import com.server.common.utils.DateUtils
import com.server.sp.enums.SpMomentMediaType
import com.server.sp.enums.SpMomentType
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
 * So whenever Amitabh Bachhan creates a moment, we go ahead and add that moment into moments_by_following
 * table for the all the followers so that they(user, Lokesh) can see the feed from people who are
 * being followed(Amitabh Bachhan) by that user (Lokesh)
 *
 *
 * */

@Table("sp_moments_by_following")
data class SpMomentsByFollowing (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "moment_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var momentId: String,

    @PrimaryKeyColumn(name = "following_user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var followingUserId: String,

    @Column("moment_type")
    var momentType: SpMomentType,

    @Column("moment_media_type")
    var momentMediaType: SpMomentMediaType,

    @Column("challenge_id")
    var challengeId: String? = null,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column("media_details")
    var mediaDetails: String? = null,

    @Column("source_media")
    var sourceMedia: String? = null,

    @Column("moment_tagged_user_details")
    var momentTaggedUserDetails: String? = null,

)

@Table("sp_moments_by_following_by_moment_tracker")
data class SpMomentsByFollowingByMomentTracker (

    @PrimaryKeyColumn(name = "moment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var momentId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "following_user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var followingUserId: String,

    @Column("moment_type")
    var momentType: SpMomentType,

    @Column("moment_media_type")
    var momentMediaType: SpMomentMediaType,

    @Column("challenge_id")
    var challengeId: String? = null,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column("media_details")
    var mediaDetails: String? = null,

    @Column("source_media")
    var sourceMedia: String? = null,

    @Column("moment_tagged_user_details")
    var momentTaggedUserDetails: String? = null,
)


@Table("sp_moments_by_following_by_following_tracker")
data class SpMomentsByFollowingByFollowingTracker (

    @PrimaryKeyColumn(name = "following_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var followingUserId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @PrimaryKeyColumn(name = "moment_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var momentId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("moment_type")
    var momentType: SpMomentType,

    @Column("moment_media_type")
    var momentMediaType: SpMomentMediaType,

    @Column("challenge_id")
    var challengeId: String? = null,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column("media_details")
    var mediaDetails: String? = null,

    @Column("source_media")
    var sourceMedia: String? = null,

    @Column("moment_tagged_user_details")
    var momentTaggedUserDetails: String? = null,
)

fun SpMomentsByFollowing.toSpMomentsByFollowingByMomentTracker(): SpMomentsByFollowingByMomentTracker {
    this.apply {
        return SpMomentsByFollowingByMomentTracker(
            momentId = this.momentId,
            userId = this.userId,
            followingUserId = this.followingUserId,
            momentType = this.momentType,
            momentMediaType = this.momentMediaType,
            challengeId = this.challengeId,
            createdAt = this.createdAt,
            title = this.title,
            description = this.description,
            mediaDetails = this.mediaDetails,
            sourceMedia = this.sourceMedia,
            momentTaggedUserDetails = momentTaggedUserDetails,
        )
    }
}

fun SpMomentsByFollowing.toSpMomentsByFollowingByFollowingTracker(): SpMomentsByFollowingByFollowingTracker {
    this.apply {
        return SpMomentsByFollowingByFollowingTracker(
            momentId = this.momentId,
            userId = this.userId,
            followingUserId = this.followingUserId,
            momentType = this.momentType,
            momentMediaType = this.momentMediaType,
            challengeId = this.challengeId,
            createdAt = this.createdAt,
            title = this.title,
            description = this.description,
            mediaDetails = this.mediaDetails,
            sourceMedia = this.sourceMedia,
            momentTaggedUserDetails = momentTaggedUserDetails,
        )
    }
}
