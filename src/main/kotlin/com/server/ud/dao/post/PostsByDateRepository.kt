package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByDate
import com.server.ud.entities.post.PostsByDateTracker
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface PostsByDateRepository : CassandraRepository<PostsByDate?, String?> {
    fun findAllByForDate(forDate: String, pageable: Pageable): Slice<PostsByDate>
}

@Repository
interface PostsByDateTrackerRepository : CassandraRepository<PostsByDateTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<PostsByDateTracker>
}
