package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByZipcode
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostsByZipcodeRepository : CassandraRepository<PostsByZipcode?, String?> {
    @AllowFiltering
    fun findAllByPostId(postId: String): List<PostsByZipcode>
}
