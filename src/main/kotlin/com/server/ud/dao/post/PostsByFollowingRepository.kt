package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByFollowing
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostsByFollowingRepository : CassandraRepository<PostsByFollowing?, String?> {

    @Query("SELECT * FROM posts_by_following where post_id = ?0 allow filtering")
    fun findAllByPostId_V2(postId: String): List<PostsByFollowing>

}
