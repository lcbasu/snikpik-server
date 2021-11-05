package com.server.ud.entities.save

import com.server.common.utils.DateUtils
import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant


/**
 *
 * We will be creating an entry here every time save is clicked
 * No matter positive or negative. So that we can use this id for
 * updating other tables save saves_by_user and saves_by_resource
 * Like -> ID1
 * Un-Like -> ID2
 *
 * */

@Table("resource_saves")
class ResourceSave {

    @PrimaryKeyColumn(name = "save_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var saveId: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow()

    @PrimaryKeyColumn(name = "resource_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var resourceId: String? = null

    @PrimaryKeyColumn(name = "resource_type", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var resourceType: ResourceType? = null

    @PrimaryKeyColumn(name = "user_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null

    // True or false based on save or unsave
    @Column("save_value")
    var saveValue: Boolean? = false
}

