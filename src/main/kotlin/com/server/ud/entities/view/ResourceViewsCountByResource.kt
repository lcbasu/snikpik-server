package com.server.ud.entities.view

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("resource_views_count_by_resource")
class ResourceViewsCountByResource {

    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String? = null

    @Column("views_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var viewsCount: Long? = null
}
