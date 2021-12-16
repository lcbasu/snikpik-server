package com.server.ud.dao.location

import com.server.ud.entities.location.Location
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : CassandraRepository<Location?, String?> {
//    @Query("select * from locations where location_id = ?0")
    fun findAllByLocationId(locationId: String?): List<Location>
}
