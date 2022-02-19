package com.server.ud.entities.integration.common

import com.server.common.utils.DateUtils
import com.server.ud.enums.IntegrationPlatform
import com.server.ud.enums.IntegrationPlatformSyncType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

// Same userId can have multiple account on same platform
// Like Akshay having 2 accounts on Instagram and 3 accounts on Facebook and 4 accounts on Pinterest
@Table("integration_account_info_by_user_id")
data class IntegrationAccountInfoByUserId (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val userId: String,

    // Adding platform name as a primary column as the same accountId can be present on multiple platforms
    @PrimaryKeyColumn(name = "platform", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val platform: IntegrationPlatform,

    // Id of the account
    @PrimaryKeyColumn(name = "account_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val accountId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    val createdAt: Instant = DateUtils.getInstantNow(),

    @Column("sync_type")
    val syncType: IntegrationPlatformSyncType,

    @Column("pause_ingestion")
    val pauseIngestion: Boolean = false,

    @Column("first_ingestion_done")
    val firstIngestionDone: Boolean = false,

    @Column("user_id_on_platform")
    val userIdOnPlatform: String? = null,

    @Column("username_on_platform")
    val usernameOnPlatform: String? = null,

    @Column("email_on_platform")
    val emailOnPlatform: String? = null,

    @Column("dp_on_platform")
    val dpOnPlatform: String? = null, // MediaDetailsV2 as string

    // Instagram Specific Columns -- Start

    @Column("authorization_code")
    val authorizationCode: String? = null,

    @Column("short_lived_access_token")
    val shortLivedAccessToken: String? = null,

    @Column("long_lived_access_token")
    val longLivedAccessToken: String? = null,

    @Column("expires_in")
    val expiresIn: Long? = null, // In Seconds

    // Instagram Specific Columns -- End

)
