package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByZipcode
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostsByZipcodeRepository : CassandraRepository<PostsByZipcode?, String?> {
    @Query("SELECT * FROM posts_by_zipcode where post_id = ?0 allow filtering")
    fun findAllByPostId_V2(postId: String): List<PostsByZipcode>
}
