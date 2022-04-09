package com.server.ud.entities.like

import com.server.common.utils.DateUtils
import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("likes_by_resource")
class LikesByResource (

    // A single post could have millions of likes. Hence, partitioning that date wise
    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String,

    @PrimaryKeyColumn(name = "resource_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var resourceType: ResourceType,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null,

    @Column("like_id")
    var likeId: String? = null, // TODO: Remove this null check once processAllLikes() is called

    // True or false based on like or unlike(remove like after liking)
    var liked: Boolean,

)

@Table("likes_by_resource_tracker")
class LikesByResourceTracker (

    // A single post could have millions of likes. Hence, partitioning that date wise
    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String,

    @PrimaryKeyColumn(name = "resource_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var resourceType: ResourceType,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null,

    @Column("like_id")
    var likeId: String? = null, // TODO: Remove this null check once processAllLikes() is called

    // True or false based on like or unlike(remove like after liking)
    var liked: Boolean,

)

fun LikesByResource.toLikesByResourceTracker(): LikesByResourceTracker {
    this.apply {
        return LikesByResourceTracker(
            resourceId = resourceId,
            resourceType = resourceType,
            createdAt = createdAt,
            userId = userId,
            likeId = likeId,
            liked = liked
        )
    }
}

fun LikesByResourceTracker.toLikesByResource(): LikesByResource {
    this.apply {
        return LikesByResource(
            resourceId = resourceId,
            resourceType = resourceType,
            createdAt = createdAt,
            userId = userId,
            likeId = likeId,
            liked = liked
        )
    }
}
