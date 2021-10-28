package com.server.ud.entities

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("likes_count_by_resource")
class LikesCountByResource {

    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String? = null

    @Column("likes_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var likesCount: Long? = null
}

