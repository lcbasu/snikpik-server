package com.server.ud.entities.auth

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("otp_validation")
data class OtpValidation (

    @PrimaryKeyColumn(name = "absolute_mobile", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var absoluteMobile: String,

    @Column("created_at")
    var createdAt: Long,

    @Column("expire_at")
    var expireAt: Long,

    @Column
    var otp: String,

    @Column("login_sequence_id")
    var loginSequenceId: String,
)
