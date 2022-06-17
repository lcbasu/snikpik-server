package com.server.sp.entities.moment

import com.server.common.utils.DateUtils
import com.server.sp.enums.SpMomentMediaType
import com.server.sp.enums.SpMomentType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("sp_moments")
data class SpMoment (

    @PrimaryKeyColumn(name = "moment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var momentId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("user_id")
    var userId: String,

    @Column("moment_type")
    var momentType: SpMomentType,

    @Column("moment_media_type")
    var momentMediaType: SpMomentMediaType,

    @Column("challenge_id")
    var challengeId: String? = null,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column("media_details")
    var mediaDetails: String? = null,

    @Column("source_media")
    var sourceMedia: String? = null,

    @Column("moment_tagged_user_details")
    var momentTaggedUserDetails: String? = null,
)

