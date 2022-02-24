package com.server.ud.dao.post

import com.server.ud.entities.post.LikedPostsByUser
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface LikedPostsByUserRepository : CassandraRepository<LikedPostsByUser?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<LikedPostsByUser>

    @AllowFiltering
    fun deleteByUserIdAndPostTypeAndPostId(userId: String, postType: PostType, postId: String)

    @Query("SELECT * FROM liked_posts_by_user where post_id = ?0 allow filtering")
    fun findAllByPostId_V2(postId: String): List<LikedPostsByUser>

    @AllowFiltering
    fun deleteAllByPostId(postId: String)
}
