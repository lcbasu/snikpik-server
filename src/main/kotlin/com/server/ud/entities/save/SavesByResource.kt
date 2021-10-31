package com.server.ud.entities.save

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("saves_by_resource")
class SavesByResource {

    // A single post could have millions of saves. Hence, partitioning that date wise
    @PrimaryKeyColumn(name = "resource_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var resourceId: String? = null

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var forDate: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @PrimaryKeyColumn(name = "user_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null

    // True or false based on save or un-save(remove save after saving)
    var saved: Boolean? = false

}

