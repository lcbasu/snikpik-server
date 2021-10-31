package com.server.ud.entities.save

import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("saves_by_user")
class SavesByUser {

    // A single post could have millions of saves. Hence, partitioning that date wise
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var forDate: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @PrimaryKeyColumn(name = "resource_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var resourceId: String? = null

    @PrimaryKeyColumn(name = "resource_type", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var resourceType: ResourceType? = null

    // True or false based on save or un-save(remove save after saving)
    var saved: Boolean? = false

}

