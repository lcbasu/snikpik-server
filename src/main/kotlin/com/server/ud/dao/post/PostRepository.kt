package com.server.ud.dao.post

import com.server.ud.entities.post.Post
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface PostRepository : CassandraRepository<Post?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
    @Query("select * from posts where post_id = ?0")
    fun findAllByPostId(postId: String?): List<Post>

    /**
     *
     * First try:
     *
     * Error:
     * Some clustering keys are missing: created_at, user_id, post_type
     *
     *
     * Hence, adding these columns while updating data
     * */
    @Query("UPDATE posts SET media = ?0 WHERE post_id = ?1 and created_at = ?2 and user_id = ?3 and post_type = ?4")
    fun updateMedia(media: String, postId: String, createdAt: Instant, userId: String, postType: PostType)
}
