package com.server.ud.entities.bookmark

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 *
 * This table store the data about whether a user bookmarked a resource or not
 *
 * */
@Table("bookmark_for_resource_by_user")
class BookmarkForResourceByUser (

    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    var bookmarked: Boolean,
)

