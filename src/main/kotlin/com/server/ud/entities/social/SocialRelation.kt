package com.server.ud.entities.social

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("social_relation")
class SocialRelation (

    @PrimaryKeyColumn(name = "from_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var fromUserId: String,

    @PrimaryKeyColumn(name = "to_user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var toUserId: String,

    @Column("following")
    val following: Boolean = false,
)

