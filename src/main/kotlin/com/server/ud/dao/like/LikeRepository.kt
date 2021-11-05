package com.server.ud.dao.like

import com.server.ud.entities.like.Like
import com.server.ud.entities.post.Post
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : CassandraRepository<Like?, String?> {
    @Query("select * from likes where like_id = ?0")
    fun findAllByLikeId(likeId: String?): List<Like>
}
