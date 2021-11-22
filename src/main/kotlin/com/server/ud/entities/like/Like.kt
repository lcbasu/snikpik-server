package com.server.ud.entities.like

import com.server.common.utils.DateUtils
import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant


/**
 *
 * We will be creating an entry here every time like is clicked
 * No matter positive or negative. So that we can use this id for
 * updating other tables like likes_by_user and likes_by_resource
 * Like -> ID1
 * Un-Like -> ID2
 *
 * */

@Table("likes")
class Like (

    @PrimaryKeyColumn(name = "like_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var likeId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("resource_id")
    var resourceId: String,

    @Column("resource_type")
    var resourceType: ResourceType,

    @Column("user_id")
    var userId: String,

    // True or false based on like or unlike
    var liked: Boolean,
)

