package com.server.ud.dao.social

import com.server.ud.entities.social.Follower
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FollowerRepository : CassandraRepository<Follower?, String?> {
    @Query("select * from followers where user_id = ?0")
    fun findAllFollowersForUserId(userId: String?): List<Follower>
}
