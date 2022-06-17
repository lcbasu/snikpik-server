package com.server.sp.dao.user

import com.server.sp.entities.user.SpUsersByHandle
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface SpUsersByHandleRepository : CassandraRepository<SpUsersByHandle?, String?> {
//    @Query("select * from users_by_handle where handle = ?0")
    fun findAllByHandle(handle: String): List<SpUsersByHandle>
}
