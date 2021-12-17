package com.server.ud.dao.user

import com.server.ud.entities.user.UserV2
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserV2Repository : CassandraRepository<UserV2?, String?> {
//    @Query("select * from users where user_id = ?0")
    fun findAllByUserId(userId: String): List<UserV2>

    @AllowFiltering
    @Query("SELECT * FROM users")
    fun getAll(): List<UserV2>
}
