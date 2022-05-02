package com.server.ud.dao.post

import com.server.ud.entities.post.InstagramPost
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface InstagramPostsRepository : CassandraRepository<InstagramPost?, String?> {
    fun findAllByUserIdAndAccountIdAndPostId(userId: String, accountId: String, postId: String): List<InstagramPost>
//    fun findAllByUserIdAndAccountId(userId: String, accountId: String): List<InstagramPost>
    fun findAllByUserIdAndAccountId(userId: String, accountId: String, pageable: Pageable): Slice<InstagramPost>
}
