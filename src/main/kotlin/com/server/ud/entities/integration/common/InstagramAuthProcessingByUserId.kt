package com.server.ud.entities.integration.common

import com.server.common.utils.DateUtils
import com.server.ud.dto.ConnectInstagramAccountResponse
import com.server.ud.enums.InstagramAuthProcessingState
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("instagram_auth_processing_by_user_id")
data class InstagramAuthProcessingByUserId (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "code", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var code: String,

    @Column("state")
    var state: InstagramAuthProcessingState,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),
)
