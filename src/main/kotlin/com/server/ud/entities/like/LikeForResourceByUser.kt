package com.server.ud.entities.like

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 *
 * This table store the data about whether a user liked a resource or not
 *
 * */
@Table("like_for_resource_by_user")
class LikeForResourceByUser (

    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    var liked: Boolean = false
)

