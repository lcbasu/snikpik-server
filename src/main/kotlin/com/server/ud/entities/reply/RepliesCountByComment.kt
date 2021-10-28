package com.server.ud.entities.reply

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("replies_count_by_comment")
class RepliesCountByComment {

    @PrimaryKeyColumn(name = "comment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var commentId: String? = null

    @Column("replies_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var repliesCount: Long? = null
}

