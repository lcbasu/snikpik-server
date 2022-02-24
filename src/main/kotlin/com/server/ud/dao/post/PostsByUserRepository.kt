package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByUser
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface PostsByUserRepository : CassandraRepository<PostsByUser?, String?> {
    fun findAllByUserIdAndPostType(userId: String, postType: PostType, pageable: Pageable): Slice<PostsByUser>

    @Query("SELECT * FROM posts_by_user where post_id = ?0 allow filtering")
    fun findAllByPostId_V2(postId: String): List<PostsByUser>

}
