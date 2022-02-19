package com.server.ud.entities.integration.common

import com.server.common.utils.DateUtils
import com.server.ud.enums.IntegrationPlatform
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

// User can pause the ingestion of a particular platform at any time
// We will use the time interval between the paused duration to not ingest any data
@Table("external_ingestion_pause_info")
data class ExternalIngestionPauseInfo (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val userId: String,

    // Adding platform name as a primary column as the same accountId can be present on multiple platforms
    @PrimaryKeyColumn(name = "platform", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val platform: IntegrationPlatform,

    // Id of the account
    @PrimaryKeyColumn(name = "account_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val accountId: String,

    @PrimaryKeyColumn(name = "pause_start_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val pauseStartAt: Instant = DateUtils.getInstantNow(),

    @Column("pause_end_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    val pauseEndAt: Instant? = null,

)
