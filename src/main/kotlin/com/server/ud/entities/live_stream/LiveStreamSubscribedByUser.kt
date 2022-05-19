package com.server.ud.entities.live_stream

import com.server.common.utils.DateUtils
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("live_stream_subscribed_by_user")
class LiveStreamSubscribedByUser (

    @PrimaryKeyColumn(name = "stream_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var streamId: String,

    @PrimaryKeyColumn(name = "subscriber_user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var subscriberUserId: String,

    var subscribed: Boolean = false,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),
)

