package com.server.ud.entities.auth

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("valid_token")
data class ValidToken (

    @PrimaryKeyColumn(name = "token", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var token: String,

    @Column("valid")
    var valid: Boolean,

    @Column("valid_by_login_sequence_id")
    var validByLoginSequenceId: String,

    @Column("invalid_by_login_sequence_id")
    var invalidByLoginSequenceId: String?, // Fill this in when invalidating a token
)
