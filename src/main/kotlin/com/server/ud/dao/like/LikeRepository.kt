package com.server.ud.dao.like

import com.server.ud.entities.like.Like
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : CassandraRepository<Like?, String?> {
//    @Query("select * from likes where like_id = ?0")
    fun findAllByLikeId(likeId: String?): List<Like>

    @AllowFiltering
    fun findAllByResourceId(resourceId: String): List<Like>
}
