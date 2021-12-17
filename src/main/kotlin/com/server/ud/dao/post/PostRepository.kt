package com.server.ud.dao.post

import com.server.ud.entities.post.Post
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : CassandraRepository<Post?, String?> {

//    @Query("select * from posts where post_id = ?0")
    fun findAllByPostId(postId: String?): List<Post>

    @AllowFiltering
    @Query("SELECT * FROM posts")
    fun getAll(): List<Post>
}
