package com.server.common.entities.auth

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("refresh_token")
data class RefreshToken (

    @PrimaryKeyColumn(name = "login_sequence_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var loginSequenceId: String,

    @Column("token")
    var token: String,

    @Column("absolute_mobile")
    var absoluteMobile: String,

    @Column("user_id")
    var userId: String,

    @Column("used_to_refresh")
    var usedToRefresh: Boolean,
)
