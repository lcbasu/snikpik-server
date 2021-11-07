package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.BookmarksByResource
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarksByResourceRepository : CassandraRepository<BookmarksByResource?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
