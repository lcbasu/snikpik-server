package com.server.ud.entities

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table
class UserPost(

    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var id: String? = null,

    @PrimaryKeyColumn(name = "userId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null,

//    @PrimaryKeyColumn(name = "userId", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
//    var userId: String? = null,

    @Column
    var postedAt: Long? = null,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null
)

