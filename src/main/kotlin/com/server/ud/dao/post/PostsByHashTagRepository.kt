package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByHashTag
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostsByHashTagRepository : CassandraRepository<PostsByHashTag?, String?> {

    @AllowFiltering
    fun findAllByPostId(postId: String): List<PostsByHashTag>
}
