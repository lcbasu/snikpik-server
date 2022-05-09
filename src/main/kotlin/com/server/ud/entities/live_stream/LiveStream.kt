package com.server.ud.entities.live_stream

import com.server.common.utils.DateUtils
import com.server.ud.enums.LiveStreamPlatform
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("live_streams")
data class LiveStream (

    // Partitioning on a fixed key so that it is easier to query and sort
    @PrimaryKeyColumn(name = "stream_platform", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val streamPlatform: LiveStreamPlatform,

    // This acts as the channel name as well as the stream unique key
    @PrimaryKeyColumn(name = "stream_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var streamId: String,

    @Column("streamer_user_id")
    val streamerUserId: String,

    @Column("start_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var startAt: Instant,

    @Column("end_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var endAt: Instant,

    @Column("header_imager")
    val headerImage: String, // MediaDetailsV2

    val title: String,

    @Column("sub_title")
    val subTitle: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

)
