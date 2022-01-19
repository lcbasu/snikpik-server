package com.server.ud.entities.user

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("users_by_mobile_number")
data class UserV2ByMobileNumber (
    @PrimaryKeyColumn(name = "absolute_mobile", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var absoluteMobile: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var userId: String,
)
