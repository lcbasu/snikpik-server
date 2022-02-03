package com.server.ud.entities.post

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("zipcode_by_post")
class ZipcodeByPost (
    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "zipcode", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var zipcode: String,
)
