package com.server.ud.dao.post

import com.server.ud.entities.post.BookmarkedPostsByUser
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface BookmarkedPostsByUserRepository : CassandraRepository<BookmarkedPostsByUser?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<BookmarkedPostsByUser>

    @AllowFiltering
    fun deleteByUserIdAndPostTypeAndPostId(userId: String, postType: PostType, postId: String)

    @AllowFiltering
    fun findAllByPostId(postId: String): List<BookmarkedPostsByUser>
}
