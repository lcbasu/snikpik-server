package com.server.ud.dao.location

import com.server.ud.entities.location.NearbyZipcodesByZipcode
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface NearbyZipcodesByZipcodeRepository : CassandraRepository<NearbyZipcodesByZipcode?, String?> {
//    @Query("select * from nearby_zipcodes_by_zipcode where zipcode = ?0")
    fun findAllByZipcode(zipcode: String): List<NearbyZipcodesByZipcode>
}
