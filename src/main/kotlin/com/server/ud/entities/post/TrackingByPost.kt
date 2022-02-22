package com.server.ud.entities.post

import com.server.common.utils.DateUtils
import com.server.ud.enums.PostTrackerType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("tracking_by_post")
class TrackingByPost (
    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "post_tracker_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var postTrackerType: PostTrackerType,

    @PrimaryKeyColumn(name = "tracking_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var trackingId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),
)
