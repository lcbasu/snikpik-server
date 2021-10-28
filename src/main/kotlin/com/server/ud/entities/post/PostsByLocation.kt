package com.server.ud.entities.post

import com.server.ud.enums.PostType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("posts_by_location")
class PostsByLocation {

    // Keeping a composite key to create a partition for
    // a location on daily basis
    // otherwise a single location can lead to skewed partition
    // when number of post from a single position increases
    @PrimaryKeyColumn(name = "location_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var locationId: String? = null

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var forDate: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @Indexed
    @PrimaryKeyColumn(name = "post_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var postId: String? = null

    @PrimaryKeyColumn(name = "post_type", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var postType: PostType? = null

    @Indexed
    @PrimaryKeyColumn(name = "user_id", ordinal = 5, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null

    @Column("location_name")
    val locationName: String? = null

    @Column
    val lat: Double? = null

    @Column
    val lng:Double? = null

    @Column
    var title: String? = null

    @Column
    var description: String? = null

    @Column
    var media: String? = null // MediaDetailsV2

    @Column
    var tags: String? = null // List of HashTagData

    @Column
    var categories: String? = null //  List of CategoryV2

}

