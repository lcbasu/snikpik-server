package com.server.ud.entities

import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant


/**
 *
 * We will be creating an entry here every time like is clicked
 * No matter positive or negative. So that we can use this id for
 * updating other tables like likes_by_user and likes_by_resource
 * Like -> ID1
 * Un-Like -> ID2
 *
 * */

@Table("likes")
class Like {

    @PrimaryKeyColumn(name = "like_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var likeId: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @Indexed
    @PrimaryKeyColumn(name = "resource_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var resourceId: String? = null

    @Indexed
    @PrimaryKeyColumn(name = "resource_type", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var resourceType: ResourceType? = null

    @PrimaryKeyColumn(name = "user_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null

    // True or false based on like or unlike
    @Column("like_value")
    var likeValue: Boolean? = false
}

