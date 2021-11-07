package com.server.ud.entities.user

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

// All handles owned by a user
@Table("handles_by_user")
class HandlesByUser (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "handle", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var handle: String,
)
