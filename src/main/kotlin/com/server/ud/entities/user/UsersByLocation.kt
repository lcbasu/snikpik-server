package com.server.ud.entities.user

import com.server.ud.enums.UserProfession
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("users_by_location")
class UsersByLocation {

    @PrimaryKeyColumn(name = "location_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var locationId: String? = null

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var forDate: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @Indexed
    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null

    @Column("location_name")
    val locationName: String? = null

    @Column("location_lat")
    val locationLat: Double? = null

    @Column("location_lng")
    val locationLng: Double? = null

    @Column("handle")
    var handle: String? = null

    @Column("absolute_mobile")
    var absoluteMobile: String? = null

    @Column("country_code")
    var countryCode: String? = "" // Country code

    @Column
    var uid: String? = ""

    @Column
    var anonymous: Boolean = false

    @Column
    var verified: Boolean = false

    @Column
    var profession: UserProfession? = null

    @Column("full_name")
    var fullName: String? = ""
}

