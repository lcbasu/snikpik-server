package com.server.sp.entities.moment

import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.sp.dto.SavedSpMomentResponse
import com.server.sp.enums.SpMomentMediaType
import com.server.sp.enums.SpMomentType
import com.server.sp.model.toMomentTaggedUserDetails
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("sp_moments_by_user")
data class SpMomentsByUser (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "moment_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var momentType: SpMomentType,

    @PrimaryKeyColumn(name = "moment_media_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var momentMediaType: SpMomentMediaType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "moment_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var momentId: String,

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

@Table("sp_moments_by_user_tracker")
data class SpMomentsByUserTracker (

    @PrimaryKeyColumn(name = "moment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var momentId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @PrimaryKeyColumn(name = "moment_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var momentType: SpMomentType,

    @PrimaryKeyColumn(name = "moment_media_type", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var momentMediaType: SpMomentMediaType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

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

fun SpMomentsByUserTracker.toSpMomentsByUser(): SpMomentsByUser {
    this.apply {
        return SpMomentsByUser(
            momentId = momentId,
            momentType = momentType,
            momentMediaType = momentMediaType,
            userId = userId,
            challengeId = challengeId,
            createdAt = createdAt,
            title = title,
            description = description,
            mediaDetails = mediaDetails,
            sourceMedia = sourceMedia,
            momentTaggedUserDetails = momentTaggedUserDetails,
        )
    }
}



fun SpMomentsByUser.toSpMomentsByUserTracker(): SpMomentsByUserTracker {
    this.apply {
        return SpMomentsByUserTracker(
            momentId = momentId,
            momentType = momentType,
            momentMediaType = momentMediaType,
            userId = userId,
            challengeId = challengeId,
            createdAt = createdAt,
            title = title,
            description = description,
            mediaDetails = mediaDetails,
            sourceMedia = sourceMedia,
            momentTaggedUserDetails = momentTaggedUserDetails,
        )
    }
}

fun SpMomentsByUser.toSavedSpMomentResponse(): SavedSpMomentResponse {
    this.apply {
        return SavedSpMomentResponse(
            momentId = momentId,
            momentType = momentType,
            momentMediaType = momentMediaType,
            userId = userId,
            challengeId = challengeId,
            createdAt = DateUtils.getEpoch(createdAt),
            title = title,
            description = description,
            mediaDetails = getMediaDetailsFromJsonString(mediaDetails),
            sourceMediaDetails = getMediaDetailsFromJsonString(mediaDetails),
            momentTaggedUserDetails = momentTaggedUserDetails!!.toMomentTaggedUserDetails()
        )
    }
}
