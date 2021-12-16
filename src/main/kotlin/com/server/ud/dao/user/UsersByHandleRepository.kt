package com.server.ud.dao.user

import com.server.ud.entities.user.UsersByHandle
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersByHandleRepository : CassandraRepository<UsersByHandle?, String?> {
//    @Query("select * from users_by_handle where handle = ?0")
    fun findAllByHandle(handle: String): List<UsersByHandle>
}
