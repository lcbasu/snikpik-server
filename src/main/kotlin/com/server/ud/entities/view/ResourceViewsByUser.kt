package com.server.ud.entities.view

import com.server.common.utils.DateUtils
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

/**
 *
 * Same resource can be viewed by same user multiple times on multiple occasions.
 *
 * We will count the user view on same resource multiple times as one view if the view
 * happens between X minutes
 *
 * X = 5 minutes right now.
 *
 * */
@Table("resource_views_by_user")
data class ResourceViewsByUser(

    // A single post could have millions of saves. Hence, partitioning that date wise
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "resource_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),
)
