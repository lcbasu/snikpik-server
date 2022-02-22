package com.server.ud.dao.post

import com.server.ud.entities.post.TrackingByPost
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface TrackingByPostRepository : CassandraRepository<TrackingByPost?, String?> {

    fun findAllByPostId(postId: String): List<TrackingByPost>

}
