package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.BookmarksByUser
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface BookmarksByUserRepository : CassandraRepository<BookmarksByUser?, String?> {
    @AllowFiltering
    fun findAllByResourceId(resourceId: String): List<BookmarksByUser>

    fun findAllByUserId(userId: String): List<BookmarksByUser>

    fun deleteAllByUserIdAndCreatedAtAndResourceId(userId: String, createdAt: Instant, resourceId: String)
}
