package com.server.sp.dao.moment

import com.server.sp.entities.moment.SpMoment
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface SpMomentRepository : CassandraRepository<SpMoment?, String?> {

//    @Query("select * from posts where post_id = ?0")
    fun findAllByMomentId(momentId: String?): List<SpMoment>

    fun deleteByMomentId(momentId: String)
}
