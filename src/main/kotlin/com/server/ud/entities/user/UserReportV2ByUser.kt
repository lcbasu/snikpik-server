package com.server.ud.entities.user

import com.server.common.utils.DateUtils
import com.server.ud.enums.UserReportActionType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Table("user_report_v2_by_user")
class UserReportV2ByUser (

    @PrimaryKeyColumn(name = "reported_by_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var reportedByUserId: String,

    @Enumerated(EnumType.STRING)
    @PrimaryKeyColumn(name = "action", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val action: UserReportActionType = UserReportActionType.REPORT,

    @PrimaryKeyColumn(name = "reported_for_user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var reportedForUserId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("reason")
    val reason: String? = "",

)
