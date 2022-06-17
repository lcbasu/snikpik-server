package com.server.sp.entities.user

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

// Only one row per handle
@Table("sp_users_by_handle")
class SpUsersByHandle (

    @PrimaryKeyColumn(name = "handle", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var handle: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var userId: String,
)
