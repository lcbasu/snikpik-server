package com.server.ud.dao.post

import com.server.ud.entities.post.ZipcodeByPost
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface ZipcodeByPostRepository : CassandraRepository<ZipcodeByPost?, String?> {

    fun findAllByPostId(postId: String): List<ZipcodeByPost>

}
