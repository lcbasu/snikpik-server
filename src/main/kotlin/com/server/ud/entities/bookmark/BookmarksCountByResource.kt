package com.server.ud.entities.bookmark

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("bookmarks_count_by_resource")
class BookmarksCountByResource {

    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String? = null

    @Column("bookmarks_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var bookmarksCount: Long? = null
}

