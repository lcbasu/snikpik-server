package com.server.ud.entities.save

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("saves_count_by_user")
class SavesCountByUser {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null

    @Column("saves_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var savesCount: Long? = null
}

