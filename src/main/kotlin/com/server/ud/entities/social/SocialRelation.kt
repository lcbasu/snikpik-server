package com.server.ud.entities.social

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("social_relation")
class SocialRelation (

    /**
     *
     * So Lokesh would follow Irfan Khan Official and NOT the other way around
     * (Irfan Khan can't follow Lokesh :()
     *
     * */

    @PrimaryKeyColumn(name = "from_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var fromUserId: String, // Lokesh - Logged in user

    @PrimaryKeyColumn(name = "to_user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var toUserId: String, // Irfan Khan Official

    @Column("following")
    val following: Boolean = false, // True
)

