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

@Table("user_reported_by_user")
class UserReportByUser (

    @PrimaryKeyColumn(name = "reported_by_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var reportedByUserId: String,

    @PrimaryKeyColumn(name = "reported_for_user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var reportedForUserId: String,

    @Column("action")
    @Enumerated(EnumType.STRING)
    val action: UserReportActionType = UserReportActionType.REPORT,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("reason")
    val reason: String? = "",

)
