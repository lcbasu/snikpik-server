package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByFollowing
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostsByFollowingRepository : CassandraRepository<PostsByFollowing?, String?> {
}
