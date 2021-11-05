package com.server.ud.entities.like

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 *
 * This table store the data about whether a user liked a resource or not
 *
 * */
@Table("likes_count_by_resource_and_user")
class LikesCountByResourceAndUser {

    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String? = null

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null

    @Column("likes_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var likesCount: Long? = null
}

