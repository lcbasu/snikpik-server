package com.server.ud.entities.post

import com.server.common.utils.DateUtils
import com.server.ud.enums.PostReportActionType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Table("post_report_v2_by_user")
class PostReportV2ByUser (

    @PrimaryKeyColumn(name = "reported_by_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var reportedByUserId: String,

    @Enumerated(EnumType.STRING)
    @PrimaryKeyColumn(name = "action", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val action: PostReportActionType = PostReportActionType.SPAM,

    @PrimaryKeyColumn(name = "post_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var postId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("reason")
    val reason: String? = "",

)
