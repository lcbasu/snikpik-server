package com.server.sp.entities.user

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

// All handles owned by a user
@Table("sp_handles_by_user")
class HandlesBySpUser (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "handle", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var handle: String,
)
