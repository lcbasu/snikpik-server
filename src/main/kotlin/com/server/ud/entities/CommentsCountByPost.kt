package com.server.ud.entities

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("comments_count_by_post")
class CommentsCountByPost {

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String? = null

    @Column("comments_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var commentsCount: Long? = null
}

