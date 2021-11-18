package com.server.ud.dao.social

import com.server.ud.entities.social.FollowingsByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowingsByUserRepository : CassandraRepository<FollowingsByUser?, String?> {

}
